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
import io.hotmoka.chat.beans.api.PartialMessage;

public class PartialMessageImpl implements PartialMessage {
    private String content;

    @SuppressWarnings("unused")
	private PartialMessageImpl() {}

    public PartialMessageImpl(String content) {
    	this.content = content;
    }

    @Override
    public FullMessage setFrom(String from) {
    	return new FullMessageImpl(from, content);
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
    	return "PartialMessageImpl with content " + content;
    }
}
