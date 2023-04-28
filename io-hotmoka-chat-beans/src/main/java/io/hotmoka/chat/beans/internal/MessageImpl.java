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

import io.hotmoka.chat.beans.api.Message;
import io.hotmoka.websockets.beans.AbstractDecoder;
import io.hotmoka.websockets.beans.AbstractEncoder;

public class MessageImpl implements Message {
    private String from;
    private String content;

    @SuppressWarnings("unused")
	private MessageImpl() {}

    public MessageImpl(String from, String content) {
    	this.from = from;
    	this.content = content;
    }

    @Override
    public void setFrom(String from) {
		this.from = from;
	}

    @Override
    public String getFrom() {
    	return from;
    }

    @Override
    public String getContent() {
    	return content;
    }

    @Override
    public void setContent(String content) {
		this.content = content;
	}

    @Override
    public String toString() {
    	return "MessageImpl from " + from + " with content " + content;
    }

    public static class Encoder extends AbstractEncoder<MessageImpl> {}

    public static class Decoder extends AbstractDecoder<MessageImpl> {

    	public Decoder() {
    		super(MessageImpl.class);
    	}
    }
}
