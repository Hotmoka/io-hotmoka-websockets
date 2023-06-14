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

	protected AbstractRpcMessageJsonRepresentation(M message) {
		this.type = message.getType();
		this.id = message.getId();
	}

	/**
	 * Yields the type of the represented message.
	 * 
	 * @return the type
	 */
	protected final String getType() {
		return type;
	}

	/**
	 * Yields the id of the represented message.
	 * 
	 * @return the id
	 */
	protected final String getId() {
		return id;
	}
}