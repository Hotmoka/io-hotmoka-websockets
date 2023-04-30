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

import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.chat.beans.internal.FullMessageImpl;
import io.hotmoka.chat.beans.internal.MessageDecoder;
import io.hotmoka.chat.beans.internal.PartialMessageImpl;
import io.hotmoka.websockets.beans.AbstractEncoder;

/**
 * A provider of messages.
 */
public interface Messages {

	static Message partial(String content) {
		return new PartialMessageImpl(content);
	}

	static Message full(String from, String content) {
		return new FullMessageImpl(from, content);
	}

	static class Encoder extends AbstractEncoder<Message> {}

    static class Decoder extends MessageDecoder {}
}