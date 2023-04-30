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
import java.net.URL;
import java.util.logging.LogManager;

public class Main {

	public static void main(String[] args) throws Exception {
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