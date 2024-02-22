/*
Copyright 2024 Fausto Spoto

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

package io.hotmoka.websockets.client;

import io.hotmoka.websockets.client.internal.RPCMessageQueuesContainerImpl;

/**
 * Providers of {@link RPCMessageQueuesContainer}.
 */
public abstract class RPCMessageQueuesContainers {
	private RPCMessageQueuesContainers() {}

	/**
	 * Yields a new container.
	 * 
	 * @param timeout the time (in milliseconds) allowed for a call to the network service;
	 *                beyond that threshold, a timeout exception is thrown
	 * @return the container
	 */
	public static RPCMessageQueuesContainer of(long timeout) {
		return new RPCMessageQueuesContainerImpl(timeout);
	}
}