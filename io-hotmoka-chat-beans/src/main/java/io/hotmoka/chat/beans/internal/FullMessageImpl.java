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

import io.hotmoka.chat.beans.api.FullMessage;

public class FullMessageImpl implements FullMessage {
    private final String from;
    private final String content;

    public FullMessageImpl(String from, String content) {
    	if (from == null || content == null)
    		throw new NullPointerException();

    	this.from = from;
    	this.content = content;
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
    public String toString() {
    	return "FullMessageImpl from " + from + " with content " + content;
    }

    public static class GsonHelper {
    	private String from;
        private String content;

        public FullMessageImpl toBean() {
        	return new FullMessageImpl(from, content);
        }
    }
}
