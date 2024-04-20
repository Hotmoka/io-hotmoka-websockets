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

package io.hotmoka.websockets.client.internal;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.hotmoka.annotations.ThreadSafe;
import io.hotmoka.websockets.beans.api.ExceptionMessage;
import io.hotmoka.websockets.beans.api.RpcMessage;

/**
 * Implementation of a container of a queue of messages for each message id. When a message for that id arrives,
 * it gets dispatched to the waiting threads for that id.
 */
@ThreadSafe
class RPCMessageQueuesContainer {
	private final long timeout;
	private final ConcurrentMap<String, BlockingQueue<RpcMessage>> queues = new ConcurrentHashMap<>();

	private final static Logger LOGGER = Logger.getLogger(RPCMessageQueuesContainer.class.getName());

	/**
	 * Creates the container.
	 * 
	 * @param timeout the time (in milliseconds) allowed for a call to the network service;
	 *                beyond that threshold, a timeout exception is thrown
	 */
	RPCMessageQueuesContainer(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * Yields the identifier for the next message.
	 * 
	 * @return the identifier
	 */
	final String nextId() {
		String id = UUID.randomUUID().toString();
		queues.put(id, new ArrayBlockingQueue<>(10));
		return id;
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param id the identifier
	 * @param processSuccess a function that defines how to generate the replied value from the RPC message
	 * @param processException a predicate that determines if an exception message is accepted for the RPC message
	 * @return the replied value
	 * @throws Exception if the execution of the message led into this exception
	 */
	final <T> T waitForResult(String id, Function<RpcMessage, T> processSuccess, Predicate<ExceptionMessage> processException) throws Exception {
		final long startTime = System.currentTimeMillis();

		while (true) {
			try {
				RpcMessage message = queues.get(id).poll(timeout - (System.currentTimeMillis() - startTime), TimeUnit.MILLISECONDS);
				if (message == null) { // time-out
					queues.remove(id);
					throw new TimeoutException();
				}

				var result = processSuccess.apply(message);
				if (result != null) {
					queues.remove(id);
					return result;
				}

				if (message instanceof ExceptionMessage em) {
					if (processException.test(em)) {
						Exception exc;
						try {
							exc = em.getExceptionClass().getConstructor(String.class).newInstance(em.getMessage());
						}
						catch (Exception e) {
							LOGGER.warning("remote: cannot instantiate the exception type: " + em.getExceptionClass().getName() + ": " + e.getMessage());
							continue;
						}

						queues.remove(id);
						throw exc;
					}

					LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
				}
				else
					LOGGER.warning("remote: received unexpected message of type " + message.getClass().getName());
			}
			catch (InterruptedException e) {
				queues.remove(id);
				throw e;
			}
		}
	}

	/**
	 * Notifies the given message to the waiting queue for its identifier.
	 * 
	 * @param message the message to notify
	 */
	void notifyResult(RpcMessage message) {
		if (message != null) {
			var queue = queues.get(message.getId());
			if (queue != null) {
				if (!queue.offer(message))
					LOGGER.log(Level.SEVERE, "remote: could not enqueue a message since the queue is full");
			}
			else
				LOGGER.warning("remote: received a message of type " + message.getClass().getName() + " but its id " + message.getId() + " has no corresponding waiting queue");
		}
	}
}