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

package io.hotmoka.websockets.beans.internal.gson;

import java.util.Optional;

import io.hotmoka.websockets.beans.AbstractRpcMessageJsonRepresentation;
import io.hotmoka.websockets.beans.api.ExceptionMessage;
import io.hotmoka.websockets.beans.api.InconsistentJsonException;
import io.hotmoka.websockets.beans.internal.ExceptionMessageImpl;

/**
 * The JSON representation of an {@link ExceptionMessage}.
 */
public abstract class ExceptionMessageJson extends AbstractRpcMessageJsonRepresentation<ExceptionMessage> {
	private final String className;
	private final String message;

	protected ExceptionMessageJson(ExceptionMessage message) {
		super(message);

		this.className = message.getExceptionClass().getName();
		this.message = message.getMessage().orElse(null);
	}

	public String getClassName() {
		return className;
	}

	public Optional<String> getMessage() {
		return Optional.ofNullable(message);
	}

	@Override
	public ExceptionMessage unmap() throws ClassNotFoundException, ClassCastException, InconsistentJsonException {
		return new ExceptionMessageImpl(this);
	}

	@Override
	protected String getExpectedType() {
		return ExceptionMessage.class.getName();
	}
}