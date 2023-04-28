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

import io.hotmoka.websockets.beans.internal.AbstractDecoder;
import io.hotmoka.websockets.beans.internal.AbstractEncoder;

public class Message {
    private String from;
    private String content;

    @SuppressWarnings("unused")
	private Message() {}

    public Message(String from, String content) {
    	this.from = from;
    	this.content = content;
    }

    public void setFrom(String from) {
		this.from = from;
	}

    public String getFrom() {
    	return from;
    }

    public String getContent() {
    	return content;
    }

    public void setContent(String content) {
		this.content = content;
	}

    @Override
    public String toString() {
    	return "Message from " + from + " with content " + content;
    }

    public static class Encoder extends AbstractEncoder<Message> {}

    public static class Decoder extends AbstractDecoder<Message> {

    	public Decoder() {
    		super(Message.class);
    	}
    }
}
