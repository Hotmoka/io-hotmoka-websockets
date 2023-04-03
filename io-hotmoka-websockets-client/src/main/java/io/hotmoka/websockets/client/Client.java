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

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.glassfish.tyrus.client.ClientManager;

import io.hotmoka.websockets.beans.Message;
import io.hotmoka.websockets.client.internal.ChatClientEndpoint;
import jakarta.websocket.ClientEndpointConfig;

public class Client implements AutoCloseable {
	private final CountDownLatch latch = new CountDownLatch(1);

	public Client() throws Exception {
		var config = ClientEndpointConfig.Builder.create()
			.encoders(List.of(Message.Encoder.class))
			.decoders(List.of(Message.Decoder.class))
			.build();

		ClientManager.createClient().connectToServer(
			new ChatClientEndpoint(this),
			config,
			new URI("ws://localhost:8025/websockets/chat/fausto"));

		latch.await(100, TimeUnit.SECONDS);
	}

	public void stop() {
		latch.countDown();
	}

	@Override
	public void close() throws Exception {
	}
}