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

import java.io.IOException;
import java.net.URISyntaxException;

import io.hotmoka.chat.client.api.ChatClient;
import io.hotmoka.chat.client.internal.ChatClientImpl;
import jakarta.websocket.DeploymentException;

/**
 * Suppliers of chat clients.
 */
public interface ChatClients {

	/**
	 * Yields a new chat client for a user with the given name.
	 * 
	 * @param username the name of the user
	 * @return the chat client
	 * @throws DeploymentException if the client could no be deployed
	 * @throws IOException if there was an I/O error
	 * @throws URISyntaxException if the syntax of the URI contacted by the client is incorrect (this depends on {@code username})
	 */
	static ChatClient withUsername(String username) throws DeploymentException, IOException, URISyntaxException {
		return new ChatClientImpl(username);
	}
}