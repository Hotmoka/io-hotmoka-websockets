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

package io.hotmoka.websockets.beans;

import java.util.Objects;

import io.hotmoka.websockets.beans.api.JsonRepresentation;
import io.hotmoka.websockets.beans.api.RpcMessage;

/**
 * The Json representation of an RPC message. It includes a {@code type} property, specifying
 * the type of the message, and an {@code id} property, that can be used to match a message
 * to its result message.
 *
 * @param <M> the type of the represented RPC message
 */
public abstract class AbstractRpcMessageJsonRepresentation<M extends RpcMessage> implements JsonRepresentation<M> {
	private String type;
	private String id;

	/**
	 * Creates the representation.
	 * 
	 * @param message the message for which the representation is created.
	 */
	protected AbstractRpcMessageJsonRepresentation(M message) {
		this.type = message.getType();
		this.id = message.getId();
	}

	/**
	 * Yields the type of the represented message.
	 * 
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * Yields the id of the represented message.
	 * 
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * The type expected for this representation. Normally, this should
	 * coincide with {@link #getType()}.
	 * 
	 * @return the expected type
	 */
	protected abstract String getExpectedType();

	/**
	 * Determines if the type of this message is the expected one.
	 * 
	 * @return true if an donly if that condition holds
	 */
	boolean isTypeConsistent() {
		return Objects.equals(getExpectedType(), getType());
	}
}