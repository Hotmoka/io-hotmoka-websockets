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

import io.hotmoka.websockets.client.api.ClientEndpoint;
import io.hotmoka.websockets.client.api.WebSocketClient;
import jakarta.websocket.Endpoint;

/**
 * Partial implementation of a websocket client endpoint.
 *
 * @param <C> the type of the client this endpoint works for
 */
public abstract class AbstractClientEndpoint<C extends WebSocketClient> extends Endpoint implements ClientEndpoint<C> {
	private final C client;

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
}
