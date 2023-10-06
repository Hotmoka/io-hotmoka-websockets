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

import java.nio.ByteBuffer;

/**
 * An exception thrown when JSON cannot be decoded into a value.
 */
@SuppressWarnings("serial")
public class DecodeException extends jakarta.websocket.DecodeException {

	/**
     * Constructor with the binary data that could not be decoded, and the reason why it failed to be, and the cause.
     * The buffer may represent the whole message, or the part of the message most relevant to the decoding error,
     * depending whether the application is using one of the streaming methods or not.
     *
     * @param bb the byte buffer containing the (part of) the message that could not be decoded
     * @param message the reason for the failure
     * @param cause the cause of the error
     */
    public DecodeException(ByteBuffer bb, String message, Throwable cause) {
        super(bb, message, cause);
    }

    /**
     * Constructor with the text data that could not be decoded, and the reason why it failed to be, and the cause. The
     * encoded string may represent the whole message, or the part of the message most relevant to the decoding error,
     * depending whether the application is using one of the streaming methods or not.
     *
     * @param encodedString the string representing the (part of) the message that could not be decoded
     * @param message the reason for the failure
     * @param cause the cause of the error
     */
    public DecodeException(String encodedString, String message, Throwable cause) {
        super(encodedString, message, cause);
    }

    /**
     * Constructs a DecodedException with the given ByteBuffer that cannot be decoded, and reason why. The buffer may
     * represent the whole message, or the part of the message most relevant to the decoding error, depending whether
     * the application is using one of the streaming methods or not.
     *
     * @param bb the byte buffer containing the (part of) the message that could not be decoded
     * @param message the reason for the failure
     */
    public DecodeException(ByteBuffer bb, String message) {
        super(bb, message);
    }

    /**
     * Constructs a DecodedException with the given encoded string that cannot be decoded, and reason why. The encoded
     * string may represent the whole message, or the part of the message most relevant to the decoding error, depending
     * whether the application is using one of the streaming methods or not.
     *
     * @param encodedString the string representing the (part of) the message that could not be decoded
     * @param message the reason for the failure
     */
    public DecodeException(String encodedString, String message) {
        super(encodedString, message);
    }
}