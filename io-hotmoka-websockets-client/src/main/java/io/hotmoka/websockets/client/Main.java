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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.glassfish.tyrus.client.ClientManager;

import io.hotmoka.websockets.beans.Message;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

public class Main {

	public static void main(String [] args) throws Exception {
		var messageLatch = new CountDownLatch(1);
		var config = ClientEndpointConfig.Builder.create()
			.encoders(List.of(Message.Encoder.class))
			.decoders(List.of(Message.Decoder.class))
			.build();
		config.getUserProperties().put("ciao", messageLatch);
		var client = ClientManager.createClient();

		client.connectToServer(new Endpoint() {

			@Override
			public void onOpen(Session session, EndpointConfig config) {
				try {
					session.addMessageHandler((MessageHandler.Whole<Message>) (message -> {
						System.out.println("Received message: " + message);
						messageLatch.countDown();
					}));
					Message message = new Message();
					message.setContent("hello websocket!");
					session.getBasicRemote().sendObject(message);
				}
				catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			}
		}, config, new URI("ws://localhost:8025/websockets/chat/fausto"));
		messageLatch.await(100, TimeUnit.SECONDS);
	}
}