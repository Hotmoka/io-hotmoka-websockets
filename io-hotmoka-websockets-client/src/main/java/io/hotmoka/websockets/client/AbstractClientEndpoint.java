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

import java.util.concurrent.Future;
import java.util.function.Consumer;

import io.hotmoka.websockets.client.api.ClientEndpoint;
import io.hotmoka.websockets.client.api.WebSocketClient;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

/**
 * Partial implementation of a websocket client endpoint.
 *
 * @param <C> the type of the client this endpoint works for
 */
public abstract class AbstractClientEndpoint<C extends WebSocketClient> extends Endpoint implements ClientEndpoint<C> {
	private final C client;

	/**
	 * The session serving this endpoint, if any.
	 */
	private Session session;

	/**
	 * Creates a new client endpoint for the given client.
	 * 
	 * @param client the client
	 */
	protected AbstractClientEndpoint(C client) {
		this.client = client;
	}

	/**
	 * Yields the client of this endpoint.
	 * 
	 * @return the client
	 */
	protected final C getClient() {
		return client;
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
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
}