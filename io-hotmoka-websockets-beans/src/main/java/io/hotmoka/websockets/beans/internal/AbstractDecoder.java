package io.hotmoka.websockets.beans.internal;

import com.google.gson.Gson;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public abstract class AbstractDecoder<T> implements Decoder.Text<T> {
	private final static Gson gson = new Gson();

	private final Class<T> beanClass;

	protected AbstractDecoder(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public T decode(String s) throws DecodeException {
		try {
			return gson.fromJson(s, beanClass);
		}
		catch (Exception e) {
			throw new DecodeException(s, "could not decode a " + beanClass.getName(), e);
		}
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