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

package io.hotmoka.chat.beans.internal;

import io.hotmoka.chat.beans.Messages;
import io.hotmoka.chat.beans.api.FullMessage;
import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.websockets.beans.JsonRepresentation;

/**
 * The JSON representation of a {@link Message}.
 */
public abstract class MessageJson implements JsonRepresentation<Message> {
	private String from;
	private String content;

	/**
	 * Used by Gson.
	 */
	protected MessageJson() {}

	/**
	 * Creates the Json.
	 * 
	 * @param message the message whose Json is created
	 */
	protected MessageJson(Message message) {
		this.content = message.getContent();

		if (message instanceof FullMessage)
			this.from = ((FullMessage) message).getFrom();
	}

	@Override
	public Message unmap() {
		return from == null ? Messages.partial(content) : Messages.full(from, content);
	}
}