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
 * An exception thrown when a value cannot be encoded into JSON.
 */
@SuppressWarnings("serial")
public class EncodeException extends jakarta.websocket.EncodeException {

	/**
     * Constructor with the object being encoded, and the reason why it failed to be.
     *
     * @param object the object that could not be encoded
     * @param message the reason for the failure
     */
	public EncodeException(Object object, String message) {
		super(object, message);
	}

    /**
     * Constructor with the object being encoded, and the reason why it failed to be, and the cause.
     *
     * @param object the object that could not be encoded
     * @param message the reason for the failure
     * @param cause the cause of the problem
     */
	public EncodeException(Object object, String message, Throwable cause) {
		super(object, message, cause);
	}
}