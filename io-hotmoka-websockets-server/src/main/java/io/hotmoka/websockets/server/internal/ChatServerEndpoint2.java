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

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.hotmoka.websockets.beans.Message;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

public class ChatServerEndpoint2 extends Endpoint {
	private ChatServer2 server;
	private Session session;
    private static Set<ChatServerEndpoint2> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    public ChatServerEndpoint2() {
    	System.out.println("initialized");
    }

    void setServer(ChatServer2 server) {
    	this.server = server;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    	System.out.println("onOpen. Endpoint: " + this.hashCode() +
    			", session: " + session.hashCode() +
    			", server: " + server.hashCode());
    	this.session = session;
    	chatEndpoints.add(this);
    	String username = session.getPathParameters().get("username");
    	users.put(session.getId(), username);

    	session.addMessageHandler(new MessageHandler.Whole<Message>() {

    		@Override
    		public void onMessage(Message message) {
    			message.setFrom(username);
    			System.out.println("onMessage " + message);
    			broadcast(message);
    		}
    	});

    	Message message = new Message();
    	message.setFrom(username);
    	message.setContent("Connected!");
    	broadcast(message);
    }

	@Override
	public void onClose(Session session, CloseReason closeReason) {
    	System.out.println("onClose. Endpoint: " + this.hashCode() + ", session: " + session.hashCode());
    	chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

	@Override
    public void onError(Session session, Throwable throwable) {
    	System.out.println("onError");
    	throwable.printStackTrace();
    }

    private static void broadcast(Message message) {
    	chatEndpoints.forEach(endpoint -> {
    		synchronized (endpoint) {
    			try {
    				endpoint.session.getBasicRemote().sendObject(message);
    			}
    			catch (IOException | EncodeException e) {
    				e.printStackTrace();
    			}
    		}
    	});
    }
}
