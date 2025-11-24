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

package io.hotmoka.websockets.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import io.hotmoka.websockets.api.FailedDeploymentException;
import io.hotmoka.websockets.beans.api.InconsistentJsonException;
import io.hotmoka.websockets.client.api.ClientEndpoint;
import io.hotmoka.websockets.client.api.WebSocketClient;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.Endpoint;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

/**
 * Partial implementation of a websocket client endpoint.
 *
 * @param <C> the type of the client this endpoint works for
 */
public abstract class AbstractClientEndpoint<C extends WebSocketClient> extends Endpoint implements ClientEndpoint<C> {

	/**
	 * The timeout for the connection to the server, in milliseconds.
	 * If empty, the default timeout of Tyrus will be used.
	 */
	private final OptionalInt timeout;

	private final static Logger LOGGER = Logger.getLogger(AbstractClientEndpoint.class.getName());

	/**
	 * Creates the endpoint, specifying the timeout for the connection to the server.
	 * 
	 * @param timeout the timeout for the connection to the server, in milliseconds
	 */
	protected AbstractClientEndpoint(int timeout) {
		this.timeout = OptionalInt.of(timeout);
	}

	/**
	 * Creates the endpoint, using the default timeout for the connection to the server.
	 */
	protected AbstractClientEndpoint() {
		this.timeout = OptionalInt.empty();
	}

	@Override
	public void onError(Session session, Throwable throwable) {
		if (throwable instanceof DecodeException e && e.getCause() instanceof InconsistentJsonException ee)
			LOGGER.log(Level.WARNING, e.getMessage() + ": " + ee.getMessage());
		else
			LOGGER.log(Level.SEVERE, "websocket endpoint " + getClass().getName(), throwable);

		super.onError(session, throwable);
	}

	/**
	 * Deploys this endpoint at the given URI, with the given decoders (inputs) and encoders (outputs).
	 * 
	 * @param uri the URI
	 * @param coders the encoders or decoders
	 * @return the resulting session
	 * @throws FailedDeploymentException if the endpoint cannot be deployed
	 * @throws InterruptedException if the deployment has been interrupted
	 */
	@SuppressWarnings("unchecked")
	protected Session deployAt(URI uri, Class<?>... coders) throws FailedDeploymentException, InterruptedException {
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

		var config = ClientEndpointConfig.Builder.create()
			.decoders(inputs)
			.encoders(outputs)
			.build();

		ClientManager client = ClientManager.createClient();
		timeout.ifPresent(threshold -> client.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, threshold));

		try {
			return client.connectToServer(this, config, uri);
		}
		catch (DeploymentException e) {
			// we catch the situation when DeploymentException has InterruptedException as cause and we throw it explicitly
			var cause = e.getCause();
			if (cause instanceof InterruptedException ie)
				throw ie;
			else
				throw new FailedDeploymentException(e);
		}
		catch (IOException e) {
			throw new FailedDeploymentException(e);
		}
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
	 * @throws IOException if an IOException occurs, for instance, the connection has been closed
	 * @throws EncodeException if there was a problem encoding the message object
	 */
	protected void sendObject(Session session, Object object) throws IOException, EncodeException {
		Objects.requireNonNull(session);
		Objects.requireNonNull(object);

		try {
			session.getBasicRemote().sendObject(object);
		}
		catch (RuntimeException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Sends the given object, asynchronously, with the given session.
	 * 
	 * @param session the session
	 * @param object the object to send
	 * @return the future that can be used to wait for the operation to complete
	 * @throws IOException if an IOException occurs, for instance, the connection has been closed
	 */
	protected Future<Void> sendObjectAsync(Session session, Object object) throws IOException {
		Objects.requireNonNull(session);
		Objects.requireNonNull(object);

		try {
			return session.getAsyncRemote().sendObject(object);
		}
		catch (RuntimeException e) {
			throw new IOException(e.getMessage());
		}
	}
}