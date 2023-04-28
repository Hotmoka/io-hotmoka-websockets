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

import io.hotmoka.chat.beans.Message;
import io.hotmoka.websockets.client.AbstractClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

public class ChatClientEndpoint extends AbstractClientEndpoint<ChatClient> {

	ChatClientEndpoint(ChatClient client) {
		super(client);
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("onOpen");
		session.addMessageHandler((MessageHandler.Whole<Message>) this::messageHandler);

		try {
			// the server will fill in the username
			session.getBasicRemote().sendObject(new Message(null, "hello websocket!"));
		}
		catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("onClose");
	}

	private void messageHandler(Message message) {
		System.out.println("Received message: " + message);
	}
}
