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
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Future;

import org.glassfish.tyrus.spi.ServerContainer;
import org.glassfish.tyrus.spi.ServerContainerFactory;

import io.hotmoka.websockets.server.api.WebSocketServer;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig;

/**
 * Partial implementation of a websocket server.
 */
public abstract class AbstractWebSocketServer implements WebSocketServer {
	private final ServerContainer container;

	/**
	 * Deploys a new websocket server.
	 * 
	 * @throws DeploymentException if the websocket server could not be deployed
	 */
	protected AbstractWebSocketServer() throws DeploymentException {
		container = ServerContainerFactory.createServerContainer(new HashMap<>());
	}

	/**
	 * Starts the server container with the given endpoints. This is typically called from
	 * the constructors of subclasses.
	 * 
	 * @param path the path where the endpoints must be deployed
	 * @param port the port at which the endpoint must be deployed
	 * @param configs the configurations of the endpoints
	 * @throws DeploymentException if some endpoint cannot be deployed
	 * @throws IOException if an I/O error occurs
	 */
	protected void startContainer(String path, int port, ServerEndpointConfig... configs) throws DeploymentException, IOException {
		for (var config: configs)
			container.addEndpoint(config);

		container.start(path, port);
	}

	/**
	 * Stops the container. This is typically called when closing the server.
	 */
	protected void stopContainer() {
		container.stop();
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