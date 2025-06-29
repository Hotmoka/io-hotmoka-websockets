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

package io.hotmoka.websockets.client.internal;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.hotmoka.annotations.ThreadSafe;
import io.hotmoka.closeables.OnCloseHandlersManagers;
import io.hotmoka.closeables.api.OnCloseHandler;
import io.hotmoka.closeables.api.OnCloseHandlersManager;
import io.hotmoka.websockets.beans.api.ExceptionMessage;
import io.hotmoka.websockets.beans.api.ResultMessage;
import io.hotmoka.websockets.beans.api.RpcMessage;
import io.hotmoka.websockets.client.AbstractClientEndpoint;
import io.hotmoka.websockets.client.AbstractRemote;
import io.hotmoka.websockets.client.AbstractWebSocketClient;
import io.hotmoka.websockets.client.api.Remote;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

/**
 * A partial implementation of a remote object that presents a programmatic interface
 * to a service for the API of another object with the same interface,
 * 
 * @param <E> the type of the exceptions thrown if the remote gets used after being closed
 */
@ThreadSafe
public abstract class AbstractRemoteImpl<E extends Exception> extends AbstractWebSocketClient implements Remote {

	/**
	 * A map from path into the session listening to that path.
	 */
	private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

	/**
	 * The manager of the close handlers.
	 */
	private final OnCloseHandlersManager manager = OnCloseHandlersManagers.create();

	/**
	 * The time (in milliseconds) allowed for the connection to the server;
	 * beyond that threshold, a timeout exception is thrown.
	 */
	private final int timeout;

	/**
	 * Queues of messages received from the external world.
	 */
	private final RPCMessageQueuesContainer queues;

	/**
	 * True if and only if this node has been closed already.
	 */
	private final AtomicBoolean isClosed = new AtomicBoolean();

	/**
	 * Used to wait until the remote gets closed.
	 */
	private final CountDownLatch latch = new CountDownLatch(1);

	/**
	 * A description of the reason why the service has been disconnected.
	 */
	private volatile String closeReason;

	private final static Logger LOGGER = Logger.getLogger(AbstractRemoteImpl.class.getName());

	/**
	 * Creates and opens a new remote application for the API of another application
	 * whose web service is already published.
	 * 
	 * @param timeout the time (in milliseconds) allowed for a call to the network service;
	 *                beyond that threshold, a timeout exception is thrown
	 */
	protected AbstractRemoteImpl(int timeout) {
		this.timeout = timeout;
		this.queues = new RPCMessageQueuesContainer(timeout);
	}

	@Override
	public final void close() {
		close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Closed normally."));
	}

	@Override
	public final void addOnCloseHandler(OnCloseHandler what) {
		manager.addOnCloseHandler(what);
	}

	@Override
	public final void removeOnCloseHandler(OnCloseHandler what) {
		manager.removeOnCloseHandler(what);
	}

	@Override
	public final String waitUntilClosed() throws InterruptedException {
		latch.await();
		return closeReason;
	}

	/**
	 * Closes this remote. Subclasses should close their own resources
	 * and then call this method at the end. This method is never called twice,
	 * but only the first time the remote gets closed.
	 * 
	 * @param reason the reason why the remote is getting closed
	 */
	protected void closeResources(CloseReason reason) {
		closeReason = reason.getReasonPhrase();

		try {
			closeSessionsAndCallOnCloseHandlers();
		}
		finally {
			latch.countDown();
		}
	}

	/**
	 * Yields an exception to throw if {@link #ensureIsOpen()} is called
	 * and the remote was already closed.
	 * 
	 * @return the exception
	 */
	protected abstract E mkExceptionIfClosed();

	/**
	 * Hook called when an exception is received as result for an RPC.
	 * 
	 * @param message the RPC message containing the exception
	 */
	protected void onException(ExceptionMessage message) {}

	/**
	 * Adds a session at the given path starting at the given URI, connected to the
	 * endpoint resulting from the given supplier.
	 * 
	 * @param path the path
	 * @param uri the URI
	 * @param endpoint the supplier of the endpoint
	 * @throws DeploymentException if the session cannot be deployed
	 * @throws IOException if an I/O error occurs
	 */
	protected final void addSession(String path, URI uri, Supplier<AbstractRemote<?>.Endpoint> endpoint) throws DeploymentException, IOException {
		sessions.put(path, endpoint.get().deployAt(uri.resolve(path)));
	}

	/**
	 * Yields the session at the given path.
	 * 
	 * @param path the path
	 * @return the session
	 */
	protected final Session getSession(String path) {
		return sessions.get(path);
	}

	/**
	 * Ensures that this node is currently open.
	 * 
	 * @throws E if this remote is already closed
	 */
	protected final void ensureIsOpen() throws E {
		if (isClosed.get())
			throw mkExceptionIfClosed();
	}

	/**
	 * Notifies the given message to the waiting queue for its identifier.
	 * 
	 * @param message the message to notify
	 */
	protected void notifyResult(RpcMessage message) {
		if (message instanceof ExceptionMessage em)
			onException(em);
		else if (message == null) {
			LOGGER.log(Level.SEVERE, "unexpected null message");
			return;
		}

		queues.notifyResult(message);
	}

	/**
	 * Yields the identifier for the next message.
	 * 
	 * @return the identifier
	 */
	protected final String nextId() {
		return queues.nextId();
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <M> the type of the expected message
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @return the replied value
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T> T waitForResult(String id, Class<? extends ResultMessage<T>> messageClass) throws InterruptedException, TimeoutException {
		return queues.waitForResult(id, messageClass);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> an exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1) throws InterruptedException, TimeoutException, E1 {
		return queues.waitForResult(id, messageClass, exceptionClass1);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> a first exception type that could be received
	 * @param <E2> a second exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @param exceptionClass2 the class of {@code E2}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws E2 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception, E2 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2) throws InterruptedException, TimeoutException, E1, E2 {
		return queues.waitForResult(id, messageClass, exceptionClass1, exceptionClass2);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> a first exception type that could be received
	 * @param <E2> a second exception type that could be received
	 * @param <E3> a third exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @param exceptionClass2 the class of {@code E2}
	 * @param exceptionClass3 the class of {@code E3}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws E2 if the received message is an exception of this type
	 * @throws E3 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3) throws InterruptedException, TimeoutException, E1, E2, E3 {
		return queues.waitForResult(id, messageClass, exceptionClass1, exceptionClass2, exceptionClass3);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> a first exception type that could be received
	 * @param <E2> a second exception type that could be received
	 * @param <E3> a third exception type that could be received
	 * @param <E4> a fourth exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @param exceptionClass2 the class of {@code E2}
	 * @param exceptionClass3 the class of {@code E3}
	 * @param exceptionClass4 the class of {@code E4}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws E2 if the received message is an exception of this type
	 * @throws E3 if the received message is an exception of this type
	 * @throws E4 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4) throws InterruptedException, TimeoutException, E1, E2, E3, E4 {
		return queues.waitForResult(id, messageClass, exceptionClass1, exceptionClass2, exceptionClass3, exceptionClass4);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> a first exception type that could be received
	 * @param <E2> a second exception type that could be received
	 * @param <E3> a third exception type that could be received
	 * @param <E4> a fourth exception type that could be received
	 * @param <E5> a fifth exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @param exceptionClass2 the class of {@code E2}
	 * @param exceptionClass3 the class of {@code E3}
	 * @param exceptionClass4 the class of {@code E4}
	 * @param exceptionClass5 the class of {@code E5}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws E2 if the received message is an exception of this type
	 * @throws E3 if the received message is an exception of this type
	 * @throws E4 if the received message is an exception of this type
	 * @throws E5 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5) throws InterruptedException, TimeoutException, E1, E2, E3, E4, E5 {
		return queues.waitForResult(id, messageClass, exceptionClass1, exceptionClass2, exceptionClass3, exceptionClass4, exceptionClass5);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> a first exception type that could be received
	 * @param <E2> a second exception type that could be received
	 * @param <E3> a third exception type that could be received
	 * @param <E4> a fourth exception type that could be received
	 * @param <E5> a fifth exception type that could be received
	 * @param <E6> a sixth exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @param exceptionClass2 the class of {@code E2}
	 * @param exceptionClass3 the class of {@code E3}
	 * @param exceptionClass4 the class of {@code E4}
	 * @param exceptionClass5 the class of {@code E5}
	 * @param exceptionClass6 the class of {@code E6}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws E2 if the received message is an exception of this type
	 * @throws E3 if the received message is an exception of this type
	 * @throws E4 if the received message is an exception of this type
	 * @throws E5 if the received message is an exception of this type
	 * @throws E6 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception, E6 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5, Class<E6> exceptionClass6) throws InterruptedException, TimeoutException, E1, E2, E3, E4, E5, E6 {
		return queues.waitForResult(id, messageClass, exceptionClass1, exceptionClass2, exceptionClass3, exceptionClass4, exceptionClass5, exceptionClass6);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> a first exception type that could be received
	 * @param <E2> a second exception type that could be received
	 * @param <E3> a third exception type that could be received
	 * @param <E4> a fourth exception type that could be received
	 * @param <E5> a fifth exception type that could be received
	 * @param <E6> a sixth exception type that could be received
	 * @param <E7> a seventh exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @param exceptionClass2 the class of {@code E2}
	 * @param exceptionClass3 the class of {@code E3}
	 * @param exceptionClass4 the class of {@code E4}
	 * @param exceptionClass5 the class of {@code E5}
	 * @param exceptionClass6 the class of {@code E6}
	 * @param exceptionClass7 the class of {@code E7}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws E2 if the received message is an exception of this type
	 * @throws E3 if the received message is an exception of this type
	 * @throws E4 if the received message is an exception of this type
	 * @throws E5 if the received message is an exception of this type
	 * @throws E6 if the received message is an exception of this type
	 * @throws E7 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception, E6 extends Exception, E7 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5, Class<E6> exceptionClass6, Class<E7> exceptionClass7) throws InterruptedException, TimeoutException, E1, E2, E3, E4, E5, E6, E7 {
		return queues.waitForResult(id, messageClass, exceptionClass1, exceptionClass2, exceptionClass3, exceptionClass4, exceptionClass5, exceptionClass6, exceptionClass7);
	}

	/**
	 * Waits until a reply arrives for the message with the given identifier.
	 * 
	 * @param <T> the type of the replied value
	 * @param <E1> a first exception type that could be received
	 * @param <E2> a second exception type that could be received
	 * @param <E3> a third exception type that could be received
	 * @param <E4> a fourth exception type that could be received
	 * @param <E5> a fifth exception type that could be received
	 * @param <E6> a sixth exception type that could be received
	 * @param <E7> a seventh exception type that could be received
	 * @param <E8> a eighth exception type that could be received
	 * @param id the identifier
	 * @param messageClass the class of the expected message
	 * @param exceptionClass1 the class of {@code E1}
	 * @param exceptionClass2 the class of {@code E2}
	 * @param exceptionClass3 the class of {@code E3}
	 * @param exceptionClass4 the class of {@code E4}
	 * @param exceptionClass5 the class of {@code E5}
	 * @param exceptionClass6 the class of {@code E6}
	 * @param exceptionClass7 the class of {@code E7}
	 * @param exceptionClass8 the class of {@code E8}
	 * @return the replied value
	 * @throws E1 if the received message is an exception of this type
	 * @throws E2 if the received message is an exception of this type
	 * @throws E3 if the received message is an exception of this type
	 * @throws E4 if the received message is an exception of this type
	 * @throws E5 if the received message is an exception of this type
	 * @throws E6 if the received message is an exception of this type
	 * @throws E7 if the received message is an exception of this type
	 * @throws E8 if the received message is an exception of this type
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if no message arrives before timeout
	 */
	protected final <T, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception, E6 extends Exception, E7 extends Exception, E8 extends Exception> T waitForResult
		(String id, Class<? extends ResultMessage<T>> messageClass, Class<E1> exceptionClass1, Class<E2> exceptionClass2, Class<E3> exceptionClass3, Class<E4> exceptionClass4, Class<E5> exceptionClass5, Class<E6> exceptionClass6, Class<E7> exceptionClass7, Class<E8> exceptionClass8) throws InterruptedException, TimeoutException, E1, E2, E3, E4, E5, E6, E7, E8 {
		return queues.waitForResult(id, messageClass, exceptionClass1, exceptionClass2, exceptionClass3, exceptionClass4, exceptionClass5, exceptionClass6, exceptionClass7, exceptionClass8);
	}

	protected abstract class Endpoint extends AbstractClientEndpoint<AbstractRemoteImpl<E>> {

		protected Endpoint() {
			super(timeout);
		}

		@Override
		public void onOpen(Session session, EndpointConfig config) {
			addMessageHandler(session, AbstractRemoteImpl.this::notifyResult);
		}

		@Override
		public void onClose(Session session, CloseReason reason) {
			try {
				super.onClose(session, reason);
			}
			finally {
				try {
					// we close the remote since it is bound to a service that seems to be getting closed
					close(reason);
				}
				catch (Exception e) {
					LOGGER.warning("remote: cannot close " + getClass().getName() + ": " + e.getMessage());
				}
			}
		}

		protected abstract Session deployAt(URI uri) throws DeploymentException, IOException;
	}

	private void close(CloseReason reason) {
		if (!isClosed.getAndSet(true))
			closeResources(reason);
	}

	private void closeSessionsAndCallOnCloseHandlers() {
		try {
			for (var session: sessions.values()) {
				try {
					session.close();
				}
				catch (IOException e) {
					LOGGER.warning("remote: cannot close session: " + e.getMessage());
				}
			}
		}
		finally {
			manager.callCloseHandlers();
		}
	}
}