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
import java.util.Objects;
import java.util.concurrent.Future;

import io.hotmoka.websockets.client.api.WebSocketClient;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;

/**
 * A partial implementation of a websocket client.
 */
public abstract class AbstractWebSocketClient implements WebSocketClient {

	/**
	 * Creates a websocket client.
	 */
	protected AbstractWebSocketClient() {
	}

	@Override
	public void close() throws Exception {
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