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

import com.google.gson.Gson;

import jakarta.websocket.DecodeException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;

public class Message {
    private String from;
    private String content;
   	private final static Gson gson = new Gson();

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
    	return new Gson().toJson(this);
    }

    public static class Encoder implements jakarta.websocket.Encoder.Text<Message> {

        @Override
        public String encode(Message message) throws EncodeException {
            return gson.toJson(message);
        }

        @Override
        public void init(EndpointConfig endpointConfig) {
            // Custom initialization logic
        }

        @Override
        public void destroy() {
            // Close resources
        }
    }

    public static class Decoder implements jakarta.websocket.Decoder.Text<Message> {

    	@Override
    	public Message decode(String s) throws DecodeException {
    		return gson.fromJson(s, Message.class);
    	}

    	@Override
    	public boolean willDecode(String s) {
    		return s != null;
    	}

    	@Override
    	public void init(EndpointConfig endpointConfig) {
    		// Custom initialization logic
    	}

    	@Override
    	public void destroy() {
    		// Close resources
    	}
    }
}
