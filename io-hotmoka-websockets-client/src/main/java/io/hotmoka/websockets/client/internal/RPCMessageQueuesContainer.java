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
import java.util.function.Supplier;
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

	final <T, E1 extends Exception> T waitForResult(String id, Function<RpcMessage, T> processSuccess, Class<E1> exception1) throws TimeoutException, InterruptedException, E1 {
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
					throwException(id, exception1, em);
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

	final <T, E1 extends Exception, E2 extends Exception> T waitForResult(String id, Function<RpcMessage, T> processSuccess, Class<E1> exception1, Class<E2> exception2) throws TimeoutException, InterruptedException, E1, E2 {
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
					throwException(id, exception1, em);
					throwException(id, exception2, em);
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

	final <T, M extends Supplier<T>, E1 extends Exception, E2 extends Exception, E3 extends Exception> T waitForResult
			(String id, Class<M> resultSupplier, Class<E1> exception1, Class<E2> exception2, Class<E3> exception3)
					throws TimeoutException, InterruptedException, E1, E2, E3 {

		final long startTime = System.currentTimeMillis();

		while (true) {
			try {
				RpcMessage message = queues.get(id).poll(timeout - (System.currentTimeMillis() - startTime), TimeUnit.MILLISECONDS);
				if (message == null) { // time-out
					queues.remove(id);
					throw new TimeoutException();
				}

				if (resultSupplier.isAssignableFrom(message.getClass()))
					return processMessage(id, resultSupplier, message);
				else if (message instanceof ExceptionMessage em) {
					throwException(id, exception1, em);
					throwException(id, exception2, em);
					throwException(id, exception3, em);
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

	final <T, M extends Supplier<T>, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception> T waitForResult // TODO: align to this case
		(String id, Class<M> resultSupplier, Class<E1> exception1, Class<E2> exception2, Class<E3> exception3, Class<E4> exception4)
			throws TimeoutException, InterruptedException, E1, E2, E3, E4 {

		long startTime = System.currentTimeMillis();

		while (true) {
			RpcMessage message;

			try {
				message = queues.get(id).poll(timeout - (System.currentTimeMillis() - startTime), TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e) {
				queues.remove(id);
				throw e;
			}

			if (message == null) { // time-out
				queues.remove(id);
				throw new TimeoutException();
			}
			else if (resultSupplier.isAssignableFrom(message.getClass()))
				return processMessage(id, resultSupplier, message);
			else if (message instanceof ExceptionMessage em) {
				throwException(id, exception1, em);
				throwException(id, exception2, em);
				throwException(id, exception3, em);
				throwException(id, exception4, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else
				LOGGER.warning("remote: received unexpected message of type " + message.getClass().getName());
		}
	}

	final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception> T waitForResult
		(String id, Function<RpcMessage, T> processSuccess, Class<E1> exception1, Class<E2> exception2, Class<E3> exception3, Class<E4> exception4, Class<E5> exception5)
			throws TimeoutException, InterruptedException, E1, E2, E3, E4, E5 {

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
					throwException(id, exception1, em);
					throwException(id, exception2, em);
					throwException(id, exception3, em);
					throwException(id, exception4, em);
					throwException(id, exception5, em);
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

	private <T, M extends Supplier<T>> T processMessage(String id, Class<M> resultSupplier, RpcMessage message) {
		try {
			@SuppressWarnings("unchecked")
			T result = (T) resultSupplier.getMethod("get").invoke(message);
			queues.remove(id);
			return result;
		}
		catch (ReflectiveOperationException e) {
			LOGGER.log(Level.SEVERE, "remote: cannot call get() on a " + resultSupplier.getName(), e);
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException | SecurityException e) {
			LOGGER.log(Level.SEVERE, "remote: cannot call get() on a " + resultSupplier.getName(), e);
			throw e;
		}
	}

	private <E extends Exception> void throwException(String id, Class<E> exception, ExceptionMessage em) throws E {
		if (exception.isAssignableFrom(em.getExceptionClass())) {
			queues.remove(id);

			try {
				throw exception.getConstructor(String.class).newInstance(em.getMessage());
			}
			catch (ReflectiveOperationException e) {
				LOGGER.log(Level.SEVERE, "remote: cannot instantiate the exception type: " + exception.getName(), e);
				throw new RuntimeException(e);
			}
			catch (IllegalArgumentException | SecurityException e) {
				LOGGER.log(Level.SEVERE, "remote: cannot instantiate the exception type: " + exception.getName(), e);
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
					LOGGER.warning("remote: could not enqueue a message since the queue is full");
			}
			else
				LOGGER.warning("remote: received a message of type " + message.getClass().getName() + " but its id " + message.getId() + " has no corresponding waiting queue");
		}
	}
}