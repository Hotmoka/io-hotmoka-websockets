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

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.hotmoka.annotations.ThreadSafe;
import io.hotmoka.websockets.beans.api.ExceptionMessage;
import io.hotmoka.websockets.beans.api.ResultMessage;
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

	final <T> T waitForResult(String id, Class<? extends ResultMessage<T>> messageClass) throws TimeoutException, InterruptedException {
		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em)
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1)
			throws TimeoutException, InterruptedException, E1 {
	
		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception, E2 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2)
			throws TimeoutException, InterruptedException, E1, E2 {
	
		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				throwException(id, exceptionClass2, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3)
			throws TimeoutException, InterruptedException, E1, E2, E3 {
	
		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				throwException(id, exceptionClass2, em);
				throwException(id, exceptionClass3, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4)
			throws TimeoutException, InterruptedException, E1, E2, E3, E4 {

		long startTime = System.currentTimeMillis();

		while (true) {
			RpcMessage message = poll(id, startTime);

			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				throwException(id, exceptionClass2, em);
				throwException(id, exceptionClass3, em);
				throwException(id, exceptionClass4, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5)
		throws TimeoutException, InterruptedException, E1, E2, E3, E4, E5 {

		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				throwException(id, exceptionClass2, em);
				throwException(id, exceptionClass3, em);
				throwException(id, exceptionClass4, em);
				throwException(id, exceptionClass5, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception, E6 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5, Class<E6> exceptionClass6)
		throws TimeoutException, InterruptedException, E1, E2, E3, E4, E5, E6 {
	
		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				throwException(id, exceptionClass2, em);
				throwException(id, exceptionClass3, em);
				throwException(id, exceptionClass4, em);
				throwException(id, exceptionClass5, em);
				throwException(id, exceptionClass6, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception, E6 extends Exception, E7 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5, Class<E6> exceptionClass6,  Class<E7> exceptionClass7)
		throws TimeoutException, InterruptedException, E1, E2, E3, E4, E5, E6, E7 {
	
		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				throwException(id, exceptionClass2, em);
				throwException(id, exceptionClass3, em);
				throwException(id, exceptionClass4, em);
				throwException(id, exceptionClass5, em);
				throwException(id, exceptionClass6, em);
				throwException(id, exceptionClass7, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
			}
		}
	}

	final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception, E6 extends Exception, E7 extends Exception, E8 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5, Class<E6> exceptionClass6,  Class<E7> exceptionClass7, Class<E8> exceptionClass8)
		throws TimeoutException, InterruptedException, E1, E2, E3, E4, E5, E6, E7, E8 {
	
		long startTime = System.currentTimeMillis();
	
		while (true) {
			RpcMessage message = poll(id, startTime);
	
			if (message instanceof ExceptionMessage em) {
				throwException(id, exceptionClass1, em);
				throwException(id, exceptionClass2, em);
				throwException(id, exceptionClass3, em);
				throwException(id, exceptionClass4, em);
				throwException(id, exceptionClass5, em);
				throwException(id, exceptionClass6, em);
				throwException(id, exceptionClass7, em);
				throwException(id, exceptionClass8, em);
				LOGGER.warning("remote: received unexpected exception of type " + em.getExceptionClass().getName());
			}
			else {
				var maybeResult = processMessage(id, messageClass, message);
				if (maybeResult.isPresent())
					return maybeResult.get().orElse(null);
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

	private RpcMessage poll(String id, long startTime) throws TimeoutException, InterruptedException {
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
	
		return message;
	}

	/**
	 * Processes the given message.
	 * 
	 * @param <T> the type of the result carried by the message
	 * @param id the identifier of the RPC call
	 * @param messageClass the expected class of the message
	 * @param message the message
	 * @return empty if the message is not of the expected class; otherwise it carries the result of the message,
	 *         which is itself optional since methods might return null
	 */
	private <T> Optional<Optional<T>> processMessage(String id, Class<? extends ResultMessage<T>> messageClass, RpcMessage message) {
		ResultMessage<T> messageAsM;

		try {
			messageAsM = messageClass.cast(message);
		}
		catch (ClassCastException e) {
			LOGGER.warning("remote: expected message of class " + messageClass.getName() + " but received a " + message.getClass().getName());
			return Optional.empty();
		}

		queues.remove(id);

		return Optional.of(Optional.ofNullable(messageAsM.get()));
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
}