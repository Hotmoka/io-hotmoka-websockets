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

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.websockets.beans.BaseDecoder;

/**
 * A decoder for {@link io.hotmoka.chat.beans.api.Message}.
 */
public class MessageDecoder extends BaseDecoder<Message> {

	public MessageDecoder() {
		super(Message.class);
	}

	@Override
	protected Message decode(JsonElement element, Gson gson) {
		// any politics able to distinguish full from partial is fine here
		if (element.getAsJsonObject().has("from"))
			return gson.fromJson(element, FullMessageImpl.GsonHelper.class).toBean();
		else
			return gson.fromJson(element, PartialMessageImpl.GsonHelper.class).toBean();
	}
}