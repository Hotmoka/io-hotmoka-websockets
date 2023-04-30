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
import java.io.UncheckedIOException;
import java.util.List;

import io.hotmoka.chat.beans.Messages;
import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.chat.beans.api.PartialMessage;
import io.hotmoka.websockets.server.AbstractServerEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.RemoteEndpoint.Basic;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;

public class ChatServerEndpoint extends AbstractServerEndpoint<ChatServerImpl> {

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    	String username = session.getPathParameters().get("username");
    	getServer().setUsername(session.getId(), username);

    	session.addMessageHandler((MessageHandler.Whole<PartialMessage>) (message -> {
    		System.out.println("Received message: " + message);
    		broadcast(message.setFrom(username), session); // fill in info about the user
    	}));

    	broadcast(Messages.full(username, "connected!"), session);
    }

    @Override
	public void onClose(Session session, CloseReason closeReason) {
        broadcast(Messages.full(getServer().getUsername(session.getId()), "disconnected!"), session);
    }

	@Override
    public void onError(Session session, Throwable throwable) {
    	throwable.printStackTrace();
    }

	static ServerEndpointConfig config(Configurator configurator) {
		return ServerEndpointConfig.Builder.create(ChatServerEndpoint.class, "/chat/{username}")
			.encoders(List.of(Messages.Encoder.class))
			.decoders(List.of(Messages.Decoder.class))
			.configurator(configurator)
			.build();
	}

	private static void broadcast(Message message, Session session) {
    	System.out.println("Broadcasting: " + message);
    	session.getOpenSessions().stream()
    		.filter(Session::isOpen)
    		.map(Session::getBasicRemote)
    		.forEach(remote -> send(message, remote));
    }

	@Override
	protected void setServer(ChatServerImpl server) {
		super.setServer(server);
	}

	private static void send(Message message, Basic remote) {
		try {
			remote.sendObject(message);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		catch (EncodeException e) {
			throw new RuntimeException(e); // unexpected
		}
	}
}