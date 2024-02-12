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

import io.hotmoka.websockets.beans.api.RpcMessage;

/**
 * A message of an RPC message. It includes a {@code type} field specifying the type
 * of the message and an {@code id} field that can be used to match a message with its reply.
 */
public abstract class AbstractRpcMessage implements RpcMessage {
	private final String type;
	private final String id;

	/**
	 * Creates the RPC message.
	 * 
	 * @param id the identifier of the message
	 */
	protected AbstractRpcMessage(String id) {
		this.id = Objects.requireNonNull(id, "id cannot be null");
		this.type = getExpectedType();
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RpcMessage) {
			var otherRPCM = (RpcMessage) other;
			return id.equals(otherRPCM.getId()) && type.equals(otherRPCM.getType());
		}
		else
			return false;
	}

	@Override
	public final int hashCode() {
		// the id is distinguishing enough for a hashcode, so better make it final
		// and avoid that subclasses embark in complex implementations
		return id.hashCode();
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