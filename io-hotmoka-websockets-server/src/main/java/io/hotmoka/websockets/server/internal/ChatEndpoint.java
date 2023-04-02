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
import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat/{username}", decoders = Message.Decoder.class, encoders = Message.Encoder.class)
public class ChatEndpoint {
	private Session session;
    private static Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    public ChatEndpoint() {
    	System.out.println("initialized");
    }

    @OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException {
    	System.out.println("onOpen");
    	this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        broadcast(message);
	}

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException {
    	message.setFrom(users.get(session.getId()));
    	System.out.println("onMessage: " + message);
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
    	System.out.println("onClose");
    	chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
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
