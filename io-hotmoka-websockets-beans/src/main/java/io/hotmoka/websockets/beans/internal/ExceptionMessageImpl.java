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

package io.hotmoka.websockets.beans.internal;

import java.util.Objects;
import java.util.Optional;

import io.hotmoka.websockets.beans.AbstractRpcMessage;
import io.hotmoka.websockets.beans.api.ExceptionMessage;
import io.hotmoka.websockets.beans.api.InconsistentJsonException;
import io.hotmoka.websockets.beans.internal.gson.ExceptionMessageJson;

/**
 * Implementation of the network message corresponding to an exception thrown by a method call.
 */
public class ExceptionMessageImpl extends AbstractRpcMessage implements ExceptionMessage {

	/**
	 * The class of the exception.
	 */
	private final Class<? extends Exception> clazz;

	/**
	 * The message of the exception.
	 */
	private final Optional<String> message;

	/**
	 * Creates the message.
	 * 
	 * @param clazz the class of the exception
	 * @param message the message of the exception, if any
	 * @param id the identifier of the message
	 */
	public ExceptionMessageImpl(Class<? extends Exception> clazz, Optional<String> message, String id) {
		super(id);

		this.clazz = Objects.requireNonNull(clazz, "clazz cannot be null");;
		this.message = Objects.requireNonNull(message, "message cannot be null");
	}

	/**
	 * Creates a message from the given JSON representation.
	 * 
	 * @param json the json
	 * @throws InconsistentJsonException if {@code json} is inconsistent
	 * @throws ClassNotFoundException if {@code json} refers to an unknown exception class
	 * @throws ClassCastException if {@code json} refers to an exception class that is not subclass of {@code java.lang.Exception}
	 */
	public ExceptionMessageImpl(ExceptionMessageJson json) throws InconsistentJsonException, ClassNotFoundException, ClassCastException {
		super(json.getId());

		var className = json.getClassName();
		if (className == null)
			throw new InconsistentJsonException("className cannot be null");

		this.clazz = Class.forName(className).asSubclass(Exception.class);
		this.message = json.getMessage();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof ExceptionMessage em &&
			super.equals(other) && clazz == em.getExceptionClass() && message.equals(em.getMessage());
	}

	@Override
	public Class<? extends Exception> getExceptionClass() {
		return clazz;
	}

	@Override
	public Optional<String> getMessage() {
		return message;
	}

	@Override
	protected String getExpectedType() {
		return ExceptionMessage.class.getName();
	}
}