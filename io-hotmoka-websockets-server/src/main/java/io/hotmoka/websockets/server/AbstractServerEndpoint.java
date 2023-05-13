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

import io.hotmoka.websockets.server.api.ServerEndpoint;
import io.hotmoka.websockets.server.api.WebSocketServer;
import jakarta.websocket.Endpoint;

/**
 * Partial implementation of a websocket endpoint.
 *
 * @param <S> the type of the server this endpoint works for
 */
public abstract class AbstractServerEndpoint<S extends WebSocketServer> extends Endpoint implements ServerEndpoint<S> {
	private volatile S server;

	/**
	 * Creates the endpoint.
	 */
	protected AbstractServerEndpoint() {}

	/**
	 * Sets the server of this endpoint.
	 * 
	 * @param server the server
	 */
	protected void setServer(S server) {
		this.server = server;
	}

	/**
	 * Yields the server of this endpoint.
	 * 
	 * @return the server
	 */
	protected final S getServer() {
		return server;
	}
}