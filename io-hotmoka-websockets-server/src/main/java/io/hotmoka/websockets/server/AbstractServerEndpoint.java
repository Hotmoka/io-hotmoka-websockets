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

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.hotmoka.websockets.server.api.ServerEndpoint;
import io.hotmoka.websockets.server.api.WebSocketServer;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;

/**
 * Partial implementation of a websocket endpoint.
 *
 * @param <S> the type of the server this endpoint works for
 */
public abstract class AbstractServerEndpoint<S extends WebSocketServer> extends Endpoint implements ServerEndpoint<S> {
	private volatile S server;

	/**
	 * The session serving this endpoint.
	 */
	private Session session;

	/**
	 * A logger, also available for subclasses.
	 */
	protected final Logger logger = Logger.getLogger(getClass().getName());

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
	 * @param handler the handler
	 */
	protected <M> void addMessageHandler(Consumer<M> handler) {
		Session session = this.session;
		if (session == null)
			throw new IllegalStateException("no session is open at the moment");

		session.addMessageHandler((MessageHandler.Whole<M>) handler::accept);
	}

	/**
	 * Sends the given object, asynchronously, with the currently open session.
	 * 
	 * @param object the object to send
	 * @return the future that can be used to wait for the operation to complete
	 */
	protected Future<Void> sendObjectAsync(Object object) {
		Session session = this.session;
		if (session == null)
			throw new IllegalStateException("no session is open at the moment");

		return session.getAsyncRemote().sendObject(object);
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
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
	}

	@Override
    public final void onError(Session session, Throwable throwable) {
		logger.log(Level.SEVERE, "websocket error", throwable);
    }

	protected static <S extends WebSocketServer> Configurator mkConfigurator(S server) {
		return new EndpointConfigurator<>(server);
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