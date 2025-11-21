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

package io.hotmoka.websockets.server;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import io.hotmoka.websockets.beans.api.RpcMessage;
import jakarta.websocket.Session;

/**
 * Partial implementation of a websocket server with the ability of running RPC requests
 * on an executor service.
 */
public abstract class AbstractRPCWebSocketServer extends AbstractWebSocketServer {
	private final static Logger LOGGER = Logger.getLogger(AbstractRPCWebSocketServer.class.getName());

	/**
	 * An RPC request to process: it contains the request itself and the session
	 * that must be used to send back its reply.
	 */
	private static class Task {
		private final Session session;
		private final RpcMessage message;

		private Task(Session session, RpcMessage message) {
			this.session = session;
			this.message = message;
		}
	}

	/**
	 * The queue of requests to execute.
	 */
	protected final BlockingQueue<Task> tasks;

	/**
	 * The executor of the {@link #tasks}.
	 */
	private final ExecutorService executors;

	/**
	 * Creates the server. It uses a tasks queue of maximal length 1000
	 * and a number of working threads equal to three times the number of available cores.
	 */
	protected AbstractRPCWebSocketServer() {
		this(1000, Runtime.getRuntime().availableProcessors() * 3);
	}

	/**
	 * Creates the server.
	 * 
	 * @param queueSize the maximal length of the tasks queue
	 * @param nThreads the number of working threads
	 */
	protected AbstractRPCWebSocketServer(int queueSize, int nThreads) {
		this.tasks = new ArrayBlockingQueue<>(queueSize);
		this.executors = Executors.newFixedThreadPool(nThreads);
    	IntStream.range(0, nThreads).forEach(__ -> executors.execute(this::processNextTask));
	}

	@Override
    protected void closeResources() {
    	try {
    		if (executors != null)
    			executors.shutdownNow();
    	}
    	finally {
    		super.closeResources();
    	}
    }

	/**
	 * Enqueue a new request to process, eventually, with the executors of this server.
	 * The result (or exception) will be sent to the given {@code session}.
	 * 
	 * @param session the session to use to send back the result of the execution of the request
	 * @param message the request message to execute
	 */
	protected final void scheduleRequest(Session session, RpcMessage message) {
    	tasks.add(new Task(session, message));
    }

	/**
	 * Executes the given request.
	 * 
	 * @param session the session to use to send back the result of the execution of the request
	 * @param message the message of the request
	 * @throws IOException if the session is not able to send the result of the execution
	 * @throws InterruptedException if the current thread gets interrupted
	 * @throws TimeoutException if the execution times out
	 */
    protected abstract void processRequest(Session session, RpcMessage message) throws IOException, InterruptedException, TimeoutException;

	/**
	 * An infinite loop that polls the queue looking for requests to execute.
	 */
	private void processNextTask() {
		try {
			while (true) {
				Task next = tasks.take();
	
				try {
					processRequest(next.session, next.message);
				}
				catch (IOException e) {
					LOGGER.warning("request processing cannot send to session (is it closed?): " + e.getMessage());
				}
				catch (TimeoutException e) {
					LOGGER.warning("request processing timed out: " + e.getMessage());
				}
				catch (RuntimeException e) {
					LOGGER.log(Level.SEVERE, "request processing failed to process a " + next.message.getClass().getName(), e);
				}
			}
		}
		catch (InterruptedException e) {
			LOGGER.fine("request processing has been interrupted");
			Thread.currentThread().interrupt();
		}
	}
}