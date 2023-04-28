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

import io.hotmoka.chat.beans.Message;
import io.hotmoka.websockets.server.AbstractServerEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

public class ChatServerEndpoint extends AbstractServerEndpoint<ChatServer> {

	public ChatServerEndpoint() {
		System.out.println("endpoint initialization");
	}

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    	System.out.println("onOpen " + Thread.currentThread().getId());
    	String username = session.getPathParameters().get("username"); // + "[" + session.getId() + "]";
    	getServer().setUsername(session.getId(), username);

    	session.addMessageHandler((MessageHandler.Whole<Message>) (message -> {
    		message.setFrom(username); // fill in info about the user
    		System.out.println("Received message: " + message + " " + Thread.currentThread().getId());
    		broadcast(message, session);
    	}));

    	broadcast(new Message(username, "Connected!"), session);
    }

    @Override
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("onClose " + Thread.currentThread().getId());
        broadcast(new Message(getServer().getUsername(session.getId()), "Disconnected!"), session);
    }

	@Override
    public void onError(Session session, Throwable throwable) {
    	throwable.printStackTrace();
    }

	@Override
	protected void setServer(ChatServer server) {
		super.setServer(server);
	}

	private void broadcast(Message message, Session session) {
    	System.out.println("Broadcasting message: " + message);
    	session.getOpenSessions()
    		.stream()
    		.filter(Session::isOpen)
    		.map(Session::getBasicRemote)
    		.forEach(remote -> {
    			try {
    				remote.sendObject(message);
    			}
    			catch (IOException | EncodeException e) {
    				e.printStackTrace();
    			}
    		});
    }
}