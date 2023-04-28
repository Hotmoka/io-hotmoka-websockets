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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.hotmoka.chat.beans.Messages;
import io.hotmoka.websockets.server.AbstractWebSocketServer;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;

public class ChatServer extends AbstractWebSocketServer {

	/**
	 * State shared among different threads executing the endpoints handlers:
	 * it must be thread-safe.
	 */
	private final Map<String, String> usernames = new ConcurrentHashMap<>();

	/**
     * Construct a new server.
     * 
     * @throws DeploymentException 
     */
    public ChatServer() throws DeploymentException {
    	int port = 8025;
    	var sec = ServerEndpointConfig.Builder.create(ChatServerEndpoint.class, "/chat/{username}")
    			.encoders(List.of(Messages.Encoder.class))
    			.decoders(List.of(Messages.Decoder.class))
    			.configurator(new MyConfigurator())
    			.build();

    	getContainer().addEndpoint(sec);
    	try {
    		getContainer().start("/websockets", port);
    	}
    	catch (Exception e) {
            throw new DeploymentException("the server couldn't be deployed", e);
        }
    }

    String getUsername(String sessionId) {
    	return usernames.get(sessionId);
    }

    void setUsername(String sessionId, String username) {
    	usernames.put(sessionId, username);
    }

    private class MyConfigurator extends Configurator {

    	@Override
    	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
            var result = super.getEndpointInstance(endpointClass);
            if (result instanceof ChatServerEndpoint)
            	((ChatServerEndpoint) result).setServer(ChatServer.this); // we inject the server

            return result;
        }
    }
}