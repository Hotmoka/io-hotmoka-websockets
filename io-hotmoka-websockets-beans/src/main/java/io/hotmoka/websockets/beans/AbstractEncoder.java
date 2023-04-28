package io.hotmoka.websockets.beans;

import com.google.gson.Gson;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

public abstract class AbstractEncoder<T> implements Encoder.Text<T> {

	protected final static Gson gson = new Gson();

	@Override
    public String encode(T value) throws EncodeException {
    	try {
    		return gson.toJson(value);
    	}
    	catch (Exception e) {
    		throw new EncodeException(value, "could not encode bean", e);
    	}
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