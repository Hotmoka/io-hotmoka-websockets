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

package io.hotmoka.chat.client.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.hotmoka.chat.beans.Messages;
import io.hotmoka.websockets.client.AbstractWebSocketClient;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;

public class ChatClient extends AbstractWebSocketClient {
	private final Session session;

	public ChatClient(String username) throws DeploymentException, IOException, URISyntaxException {
		var endpoint = new ChatClientEndpoint(this);
		this.session = endpoint.deployAt(new URI("ws://localhost:8025/websockets/chat/" + username));
	}

	public void sendMessage(String s) throws IOException, EncodeException {
		if (session.isOpen()) {
			var message = Messages.partial(s); // the server will fill in the username
			System.out.println("sending " + message);
			session.getBasicRemote().sendObject(message);
		}
	}
}