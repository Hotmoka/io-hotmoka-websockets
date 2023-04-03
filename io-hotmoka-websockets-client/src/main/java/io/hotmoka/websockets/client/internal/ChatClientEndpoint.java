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

package io.hotmoka.websockets.client.internal;

import java.io.IOException;

import io.hotmoka.websockets.beans.Message;
import io.hotmoka.websockets.client.Client;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

public class ChatClientEndpoint extends Endpoint {

	private final Client client;

	public ChatClientEndpoint(Client client) {
		this.client = client;
	}

	protected Client getClient() {
		return client;
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		try {
			session.addMessageHandler((MessageHandler.Whole<Message>) (message -> {
				System.out.println("Received message: " + message);
				getClient().stop();
			}));
			Message message = new Message();
			message.setContent("hello websocket!");
			session.getBasicRemote().sendObject(message);
		}
		catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
	}
}
