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
import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.chat.client.api.ChatClient;
import io.hotmoka.websockets.api.FailedDeploymentException;
import io.hotmoka.websockets.client.AbstractClientEndpoint;
import io.hotmoka.websockets.client.AbstractWebSocketClient;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

public class ChatClientImpl extends AbstractWebSocketClient implements ChatClient {
	private final Session session;

	public ChatClientImpl(String username, String url) throws FailedDeploymentException {
		try {
			this.session = new ChatClientEndpoint().deployAt(new URI(url + "/websockets/chat/" + username));
		}
		catch (URISyntaxException e) {
			throw new FailedDeploymentException(e);
		}
	}

	@Override
	public void close() {
	}

	@Override
	public void sendMessage(String s) throws IOException, EncodeException {
		if (session.isOpen()) {
			var message = Messages.partial(s); // the server will fill in the username
			System.out.println("Sending " + message);
			try {
				sendObject(session, message);
			}
			catch (IOException e) {
				System.out.println("Cannot send " + message + " to session " + session.getId());
			}
		}
		else
			System.out.println("Not sending message since the session is closed");
	}

	private class ChatClientEndpoint extends AbstractClientEndpoint<ChatClientImpl> {

		private Session deployAt(URI uri) throws FailedDeploymentException {
			return deployAt(uri, Messages.Decoder.class, Messages.Encoder.class);
		}

		@Override
		public void onOpen(Session session, EndpointConfig config) {
			addMessageHandler(session, this::onReceive);
			var message = Messages.partial("hello websocket!");

			try {
				sendObject(session, message);
			}
			catch (IOException | EncodeException e) {
				System.out.println("Cannot send " + message + " to session " + session.getId());
			}
		}

		@Override
		public void onClose(Session session, CloseReason closeReason) {
			System.out.println("Session closed!");
		}

		private void onReceive(Message message) {
			System.out.println("Received " + message);
		}
	}
}