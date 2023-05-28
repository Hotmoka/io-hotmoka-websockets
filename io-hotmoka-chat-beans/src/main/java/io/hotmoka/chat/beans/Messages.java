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

package io.hotmoka.chat.beans;

import io.hotmoka.chat.beans.api.FullMessage;
import io.hotmoka.chat.beans.api.PartialMessage;
import io.hotmoka.chat.beans.internal.FullMessageImpl;
import io.hotmoka.chat.beans.internal.MessageDecoder;
import io.hotmoka.chat.beans.internal.MessageEncoder;
import io.hotmoka.chat.beans.internal.PartialMessageImpl;

/**
 * Providers of messages.
 */
public interface Messages {

	/**
	 * Yields a partial message with the given content.
	 * 
	 * @param content the content
	 * @return the partial message
	 */
	static PartialMessage partial(String content) {
		return new PartialMessageImpl(content);
	}

	/**
	 * Yields a full message from the given sender and with the given content.
	 * 
	 * @param from the sender
	 * @param content the content
	 * @return the full message
	 */
	static FullMessage full(String from, String content) {
		return new FullMessageImpl(from, content);
	}

	/**
	 * The encoder of messages.
	 */
	static class Encoder extends MessageEncoder {}

	/**
	 * The decoder of messages.
	 */
    static class Decoder extends MessageDecoder {}
}