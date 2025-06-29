/*
Copyright 2025 Fausto Spoto

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

package io.hotmoka.websockets.api;

import java.util.Objects;

/**
 * An exception thrown if a remote could not be deployed.
 */
@SuppressWarnings("serial")
public class FailedDeploymentException extends Exception {

	/**
	 * Creates the exception, with the given message.
	 * 
	 * @param message the message
	 */
	public FailedDeploymentException(String message) {
		super(Objects.requireNonNull(message));
	}

	/**
	 * Creates the exception, with the given cause.
	 * 
	 * @param cause the cause
	 */
	public FailedDeploymentException(Throwable cause) {
		super(Objects.requireNonNull(cause));
	}

	/**
	 * Creates the exception, with the given message and cause.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public FailedDeploymentException(String message, Throwable cause) {
		super(Objects.requireNonNull(message), Objects.requireNonNull(cause));
	}
}