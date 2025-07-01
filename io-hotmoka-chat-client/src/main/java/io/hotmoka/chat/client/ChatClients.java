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

package io.hotmoka.chat.client;

import io.hotmoka.chat.client.api.ChatClient;
import io.hotmoka.chat.client.internal.ChatClientImpl;
import io.hotmoka.websockets.api.FailedDeploymentException;

/**
 * Suppliers of chat clients.
 */
public interface ChatClients {

	/**
	 * Yields a new chat client for a user with the given name.
	 * It connects to a server at {@code ws://localhost:8025}.
	 * 
	 * @param username the name of the user
	 * @return the chat client
	 * @throws FailedDeploymentException if the client could no be deployed
	 */
	static ChatClient open(String username) throws FailedDeploymentException {
		return new ChatClientImpl(username, "ws://localhost:8025");
	}

	/**
	 * Yields a new chat client for a user with the given name.
	 * It connects to a server at the given URL.
	 * 
	 * @param username the name of the user
	 * @param url the url
	 * @return the chat client
	 * @throws FailedDeploymentException if the client could no be deployed
	 */
	static ChatClient open(String username, String url) throws FailedDeploymentException {
		return new ChatClientImpl(username, url);
	}
}