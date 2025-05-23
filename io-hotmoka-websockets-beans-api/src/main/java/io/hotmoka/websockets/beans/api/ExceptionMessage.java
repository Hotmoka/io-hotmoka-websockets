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

package io.hotmoka.websockets.beans.api;

import java.util.Optional;

/**
 * A network message corresponding to an exception thrown by a method call.
 */
public interface ExceptionMessage extends RpcMessage {

	/**
	 * Yields the class of the exception.
	 * 
	 * @return the class of the exception
	 */
	Class<? extends Exception> getExceptionClass();

	/**
	 * Yields the message of the exception.
	 * 
	 * @return the message, if any
	 */
	Optional<String> getMessage();
}