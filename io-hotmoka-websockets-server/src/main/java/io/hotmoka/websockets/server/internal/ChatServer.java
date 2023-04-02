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

package io.hotmoka.websockets.server.internal;

import org.glassfish.tyrus.server.Server;

import jakarta.websocket.DeploymentException;

public class ChatServer extends Server implements AutoCloseable {
	
	public ChatServer() throws DeploymentException {
		super("localhost", 8025, "/websockets", null, ChatServerEndpoint.class);
		start();
	}

	@Override
	public void close() throws Exception {
		stop();
	}
}