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

package io.hotmoka.chat.server.internal;

import java.io.IOException;
import java.util.function.Consumer;

import io.hotmoka.chat.beans.Messages;
import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.chat.beans.api.PartialMessage;
import io.hotmoka.websockets.server.AbstractServerEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig;

public class ChatServerEndpoint extends AbstractServerEndpoint<ChatServerImpl> {

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    	String username = session.getPathParameters().get("username");
    	getServer().setUsername(session.getId(), username);

    	Consumer<PartialMessage> handler = message -> {
    		System.out.println("Received " + message);
    		broadcast(message.setFrom(username), session); // fill in info about the user
    	};

    	addMessageHandler(session, handler);
    	broadcast(Messages.full(username, "connected!"), session);
    }

    @Override
	public void onClose(Session session, CloseReason closeReason) {
        broadcast(Messages.full(getServer().getUsername(session.getId()), "disconnected!"), session);
    }

	static ServerEndpointConfig config(ChatServerImpl server) {
		return simpleConfig(server, ChatServerEndpoint.class, "/chat/{username}", Messages.Decoder.class, Messages.Encoder.class);
	}

	private void broadcast(Message message, Session session) {
    	System.out.println("Broadcasting " + message);
    	session.getOpenSessions().stream()
    		.filter(Session::isOpen)
    		.forEach(_session -> send(message, _session));
    }

	private void send(Message message, Session session) {
		try {
			sendObject(session, message);
		}
		catch (EncodeException | IOException e) {
			System.out.println("Cannot send " + message + " to session " + session.getId());
		}
	}
}