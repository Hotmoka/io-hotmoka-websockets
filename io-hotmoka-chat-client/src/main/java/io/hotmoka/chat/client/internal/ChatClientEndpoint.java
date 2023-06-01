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
import java.util.List;

import org.glassfish.tyrus.client.ClientManager;

import io.hotmoka.chat.beans.Messages;
import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.websockets.client.AbstractClientEndpoint;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

class ChatClientEndpoint extends AbstractClientEndpoint<ChatClientImpl> {

	ChatClientEndpoint(ChatClientImpl client) {
		super(client);
	}

	Session deployAt(URI uri) throws DeploymentException, IOException {
		var config = ClientEndpointConfig.Builder.create()
				.encoders(List.of(Messages.Encoder.class))
				.decoders(List.of(Messages.Decoder.class))
				.build();

		return ClientManager.createClient().connectToServer(this, config, uri);
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		addMessageHandler(session, this::messageHandler);

		try {
			// the server will fill in the username
			sendObject(session, Messages.partial("hello websocket!"));
		}
		catch (IOException | EncodeException e) {
			throw new RuntimeException(e);
		}
	}

	private void messageHandler(Message message) {
		System.out.println("Received " + message);
	}
}