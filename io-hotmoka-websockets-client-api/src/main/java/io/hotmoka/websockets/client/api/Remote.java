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

package io.hotmoka.websockets.client.api;

import io.hotmoka.closeables.api.OnCloseHandlersContainer;

/**
 * A remote object that presents a programmatic interface
 * to a service for the API of another object of the same class.
 * 
 * @param <E> the type of the exceptions thrown if the remote behaves incorrectly
 */
public interface Remote<E extends Exception> extends AutoCloseable, OnCloseHandlersContainer {

	/**
	 * Waits until this remote gets closed.
	 * 
	 * @return a description of why the remote has been closed
	 * @throws InterruptedException if the current thread gets interrupted while waiting
	 */
	String waitUntilClosed() throws InterruptedException;
}