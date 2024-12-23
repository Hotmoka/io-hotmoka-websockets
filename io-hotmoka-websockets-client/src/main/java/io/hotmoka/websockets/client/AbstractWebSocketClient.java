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
import java.util.function.Function;

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

	/**
	 * Sends the given object, synchronously, with the given session.
	 * 
	 * @param session the session
	 * @param object the object to send
	 * @throws IOException if an IOException occurs, for instance, the connection has been closed
	 * @throws EncodeException if there was a problem encoding the message object
	 */
	protected void sendObject(Session session, Object object) throws IOException, EncodeException {
		sendObject(session, object, IOException::new);
	}

	/**
	 * Sends the given object, synchronously, with the given session.
	 * 
	 * @param <E> the type of the exception thrown if there is an input/output error
	 * @param session the session
	 * @param object the object to send
	 * @param exceptionSupplier the supplier of the exception to throw if there is an input/output error: it receives the message of the exception
	 * @throws E if an input/output error occurs, for instance, if the connection is closed
	 * @throws EncodeException if there was a problem encoding the message object
	 */
	protected <E extends Exception> void sendObject(Session session, Object object, Function<String, E> exceptionSupplier) throws E, EncodeException {
		Objects.requireNonNull(session);
		Objects.requireNonNull(object);

		try {
			session.getBasicRemote().sendObject(object);
		}
		catch (IOException | RuntimeException e) {
			throw exceptionSupplier.apply(e.getMessage());
		}
	}

	/**
	 * Sends the given object, asynchronously, with the given session.
	 * 
	 * @param session the session
	 * @param object the object to send
	 * @return the future that can be used to wait for the operation to complete
	 * @throws IOException if an input/output exception occurs, for instance, because the connection is closed
	 */
	protected Future<Void> sendObjectAsync(Session session, Object object) throws IOException {
		return sendObjectAsync(session, object, IOException::new);
	}

	/**
	 * Sends the given object, asynchronously, with the given session.
	 * 
	 * @param <E> the type of the exception thrown if there is an input/output error
	 * @param session the session
	 * @param object the object to send
	 * @param exceptionSupplier the supplier of the exception to throw if there is an input/output error: it receives the message of the exception
	 * @return the future that can be used to wait for the operation to complete
	 * @throws E if an input/output exception occurs, for instance, because the connection is closed
	 */
	protected <E extends Exception> Future<Void> sendObjectAsync(Session session, Object object, Function<String, E> exceptionSupplier) throws E {
		Objects.requireNonNull(session);
		Objects.requireNonNull(object);

		try {
			return session.getAsyncRemote().sendObject(object);
		}
		catch (RuntimeException e) {
			throw exceptionSupplier.apply(e.getMessage());
		}
	}
}