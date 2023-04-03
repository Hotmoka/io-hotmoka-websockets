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

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.server.TyrusServerContainer;
import org.glassfish.tyrus.spi.ServerContainer;
import org.glassfish.tyrus.spi.ServerContainerFactory;

import io.hotmoka.websockets.beans.Message;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;

public class ChatServer2 implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getClass().getName());
    private final ServerContainer server;

    /**
     * Construct a new server.
     * 
     * @throws DeploymentException 
     */
    public ChatServer2() throws DeploymentException {
    	int port = 8025;
    	var sec = ServerEndpointConfig.Builder.create(ChatServerEndpoint2.class, "/chat/{username}")
    			.encoders(List.of(Message.Encoder.class))
    			.decoders(List.of(Message.Decoder.class))
    			.configurator(new MyConfigurator())
    			.build();

    	server = ServerContainerFactory.createServerContainer(new HashMap<>());
    	server.addEndpoint(sec);
    	try {
    		server.start("/websockets", port);
    	}
    	catch (Exception e) {
            throw new DeploymentException(e.getMessage(), e);
        }

    	if (server instanceof TyrusServerContainer)
    		port = ((TyrusServerContainer) server).getPort();

    	LOGGER.info("Websocket server started. URLs all start with ws://localhost:" + port);
    }

    private class MyConfigurator extends Configurator {
    	
    	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
            var result = super.getEndpointInstance(endpointClass);
            if (result instanceof ChatServerEndpoint2)
            	((ChatServerEndpoint2) result).setServer(ChatServer2.this);

            return result;
        }
    }

	@Override
	public void close() throws Exception {
		server.stop();
		LOGGER.info("Websocket server stopped.");
	}
}