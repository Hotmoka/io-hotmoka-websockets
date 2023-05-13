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

package io.hotmoka.chat.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.LogManager;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;

/**
 * Start class of a chat client.
 */
public class Main {

	private Main() {}

	/**
	 * Entry point of a chat client.
	 * 
	 * @param args the command-line arguments
	 * @throws URISyntaxException if the contacted URI is not correct (this depends on the username in {@code args[0]}
	 * @throws DeploymentException if the server cannot be deployed
	 * @throws IOException if an I/O error occurs
	 * @throws InterruptedException if the current thread is interrupted while waiting
	 */
	public static void main(String[] args) throws DeploymentException, IOException, URISyntaxException, InterruptedException {
		if (args.length != 1) {
			System.out.println("You need to specify exactly one username");
			System.exit(-1);
		}

		try (var client = ChatClients.withUsername(args[0])) {
			client.sendMessage("hello (1/3)");
			Thread.sleep(5000);
			client.sendMessage("hello (2/3)");
			Thread.sleep(5000);
			client.sendMessage("hello (3/3)");
			Thread.sleep(5000);
		}
		catch (EncodeException e) {
			// the strings we send can be encoded into Message's
			System.out.println("unexpected exception: " + e.getMessage());
		}
		
	}

	static {
		String current = System.getProperty("java.util.logging.config.file");
		if (current == null) {
			// if the property is not set, we provide a default (if it exists)
			URL resource = Main.class.getClassLoader().getResource("logging.properties");
			if (resource != null)
				try (var is = resource.openStream()) {
					LogManager.getLogManager().readConfiguration(is);
				}
				catch (SecurityException | IOException e) {
					throw new RuntimeException("Cannot load the logging.properties file", e);
				}
		}
	}
}