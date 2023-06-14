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
 * An RPC message. It specifies its type and its id. This id can be used
 * to match a reply message with its corresponding request message.
 */
public interface RpcMessage {

	/**
	 * Yields the type of this message. This is anything that can help identify the
	 * type of the message.
	 * 
	 * @return the type
	 */
	String getType();

	/**
	 * Yields the id of the message.
	 * 
	 * @return the id
	 */
	String getId();
}