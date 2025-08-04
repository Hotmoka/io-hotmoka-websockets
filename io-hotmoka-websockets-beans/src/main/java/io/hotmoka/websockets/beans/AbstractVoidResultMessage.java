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

package io.hotmoka.websockets.beans;

import io.hotmoka.exceptions.ExceptionSupplierFromMessage;
import io.hotmoka.websockets.beans.api.VoidResultMessage;

/**
 * Implementation of a network message corresponding to the result of method call that returns no value.
 */
public abstract class AbstractVoidResultMessage extends AbstractRpcMessage implements VoidResultMessage {
	
	/**
	 * Creates the void result message.
	 * 
	 * @param id the identifier of the message
	 */
	protected AbstractVoidResultMessage(String id) {
		super(id);
	}

	/**
	 * Creates the RPC message.
	 * 
	 * @param <E> the exception to throw if {@code id} is illegal
	 * @param id the identifier of the message
	 * @param onIllegalArgs the provider of the exception to throw if {@code id} is illegal
	 * @throws E if {@code id} is illegal
	 */
	protected <E extends Exception> AbstractVoidResultMessage(String id, ExceptionSupplierFromMessage<? extends E> onIllegalArgs) throws E {
		super(id, onIllegalArgs);
	}

	@Override
	public final Void get() {
		return null;
	}
}
