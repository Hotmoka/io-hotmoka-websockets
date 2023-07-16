/*
Copyright 2023 Fausto Spoto

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.hotmoka.websockets.server;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.hotmoka.websockets.server.api.ServerEndpoint;
import io.hotmoka.websockets.server.api.WebSocketServer;
import jakarta.websocket.Decoder;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.Endpoint;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;

/**
 * Partial implementation of a websocket endpoint.
 *
 * @param <S> the type of the server this endpoint works for
 */
public abstract class AbstractServerEndpoint<S extends WebSocketServer> extends Endpoint implements ServerEndpoint<S> {
	private volatile S server;

	private final static Logger LOGGER = Logger.getLogger(AbstractServerEndpoint.class.getName());

	/**
	 * Creates the endpoint.
	 */
	protected AbstractServerEndpoint() {}

	/**
	 * Yields the server of this endpoint.
	 * 
	 * @return the server
	 */
	protected final S getServer() {
		return server;
	}

	/**
	 * Adds the given handler for incoming messages to the given session.
	 * 
	 * @param <M> the type of the messages
	 * @param session the session
	 * @param handler the handler
	 */
	protected <M> void addMessageHandler(Session session, Consumer<M> handler) {
		session.addMessageHandler((MessageHandler.Whole<M>) handler::accept);
	}

	/**
	 * Sends the given object, synchronously, with the given session.
	 * 
	 * @param session the session
	 * @param object the object to send
	 * @throws IOException if an IOException occurs
	 * @throws EncodeException if there was a problem encoding the message object
	 * @throws IllegalStateException if the connection has been closed
	 */
	protected void sendObject(Session session, Object object) throws IOException, EncodeException, IllegalStateException {
		Objects.requireNonNull(session);
		Objects.requireNonNull(object);

		try {
			session.getBasicRemote().sendObject(object);
		}
		catch (IllegalStateException e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	/**
	 * Sends the given object, asynchronously, with the given session.
	 * 
	 * @param session the session
	 * @param object the object to send
	 * @return the future that can be used to wait for the operation to complete
	 * @throws IllegalStateException if the connection has been closed
	 */
	protected Future<Void> sendObjectAsync(Session session, Object object) throws IllegalStateException {
		Objects.requireNonNull(session);
		Objects.requireNonNull(object);

		try {
			return session.getAsyncRemote().sendObject(object);
		}
		catch (IllegalStateException e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	/**
	 * Sets the server of this endpoint.
	 * 
	 * @param server the server
	 */
	void setServer(S server) {
		this.server = server;
	}

	@Override
    public final void onError(Session session, Throwable throwable) {
		LOGGER.log(Level.SEVERE, "websocket [" + getClass().getName() + "] error", throwable);
    }

	/**
	 * Yields an endpoint configuration that injects the reference to the server inside the endpoint.
	 * This allows endpoints to use the state of the server, without relying on singletons.
	 * This configurator is automatically used by the {@link #simpleConfig(WebSocketServer, Class, String, Class...)}
	 * method.
	 * 
	 * @param <S> the type of the server
	 * @param server the injected server
	 * @return the configurator
	 */
	protected static <S extends WebSocketServer> Configurator mkConfigurator(S server) {
		return new EndpointConfigurator<>(server);
	}

	/**
	 * Yields an endpoint configuration with the given decoders (inputs) and encoders (outputs).
	 * 
	 * @param <S> the type of the server
	 * @param server the server
	 * @param clazz the class of the endpoint
	 * @param subpath the subpath where the endpoint must be published 
	 * @param coders the coders and encoders
	 * @return the configuration
	 */
	@SuppressWarnings("unchecked")
	protected static <S extends WebSocketServer> ServerEndpointConfig simpleConfig(S server, Class<? extends AbstractServerEndpoint<S>> clazz, String subpath, Class<?>... coders) {
		List<Class<? extends Decoder>> inputs = Stream.of(coders)
			.filter(coder -> Decoder.class.isAssignableFrom(coder))
			.map(coder -> (Class<? extends Decoder>) coder)
			.collect(Collectors.toList());

		List<Class<? extends Encoder>> outputs = Stream.of(coders)
			.filter(coder -> Encoder.class.isAssignableFrom(coder))
			.map(coder -> (Class<? extends Encoder>) coder)
			.collect(Collectors.toList());

		Stream.of(coders)
			.filter(coder -> !inputs.contains(coder) && !outputs.contains(coder))
			.forEach(coder -> LOGGER.warning("Unknown coder " + coder + ": only encoders and decoders are allowed"));

		return ServerEndpointConfig.Builder.create(clazz, subpath)
			.decoders(inputs)
			.encoders(outputs)
			.configurator(mkConfigurator(server))
			.build();
	}

	private final static class EndpointConfigurator<S extends WebSocketServer> extends Configurator {
		private S server;

		private EndpointConfigurator(S server) {
    		this.server = server;
    	}

		@SuppressWarnings("unchecked")
		@Override
    	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
            T result = super.getEndpointInstance(endpointClass);
            if (result instanceof AbstractServerEndpoint<?>)
            	// the following cast might fail if the programmer registers in a server
            	// some AbstractServerEndpoint for the another server, of another class
            	((AbstractServerEndpoint<S>) result).setServer(server); // we inject the server

            return result;
        }
    }
}