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
import java.util.concurrent.Future;

import org.glassfish.tyrus.spi.ServerContainer;
import org.glassfish.tyrus.spi.ServerContainerFactory;

import io.hotmoka.websockets.server.api.WebSocketServer;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;

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
	 * Yields the container of this server.
	 * 
	 * @return the container
	 */
	protected final ServerContainer getContainer() {
		return container;
	}

	/**
	 * Sends the given object, synchronously, with the given session.
	 * 
	 * @param session the session
	 * @param object the object to send
	 * @throws IOException if an IOException occurs
	 * @throws EncodeException if there was a problem encoding the message object
	 */
	protected void sendObject(Session session, Object object) throws IOException, EncodeException {
		session.getBasicRemote().sendObject(object);
	}

	/**
	 * Sends the given object, asynchronously, with the given session.
	 * 
	 * @param session the session
	 * @param object the object to send
	 * @return the future that can be used to wait for the operation to complete
	 */
	protected Future<Void> sendObjectAsync(Session session, Object object) {
		return session.getAsyncRemote().sendObject(object);
	}

	@Override
	public void close() {
		container.stop();
	}
}