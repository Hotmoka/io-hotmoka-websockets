package io.hotmoka.websockets.beans;

import com.google.gson.Gson;

import io.hotmoka.websockets.beans.api.DecoderText;
import jakarta.websocket.DecodeException;
import jakarta.websocket.EndpointConfig;

/**
 * Partial implementation of a decoder from JSON strings into objects.
 *
 * @param <T> the type of the objects
 */
public abstract class AbstractDecoder<T> implements DecoderText<T> {

	/**
	 * The gson utility used for decoding.
	 */
	protected final Gson gson;

	/**
	 * The class whose objects are decoded by this decoder.
	 */
	private final Class<T> beanClass;

	/**
	 * Creates a decoder for the given class, using a brand new gson utility.
	 * 
	 * @param beanClass the class
	 */
	protected AbstractDecoder(Class<T> beanClass) {
		this(new Gson(), beanClass);
	}

	/**
	 * Creates a decoder for the given class, using the given gson utility.
	 * 
	 * @param gson the gson utility
	 * @param beanClass the class
	 */
	protected AbstractDecoder(Gson gson, Class<T> beanClass) {
		this.gson = gson;
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