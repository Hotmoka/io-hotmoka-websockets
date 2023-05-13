package io.hotmoka.websockets.beans;

import com.google.gson.Gson;

import io.hotmoka.websockets.beans.api.EncoderText;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;

/**
 * Partial implementation of an encoder from an object into a JSON string.
 *
 * @param <T> the type of the object
 */
public abstract class AbstractEncoder<T> implements EncoderText<T> {

	/**
	 * The gson utility used for the conversion.
	 */
	protected final Gson gson;

	/**
	 * Creates an encoder, using a brand new gson utility.
	 */
	protected AbstractEncoder() {
		this(new Gson());
	}

	/**
	 * Creates an encoder using the given gson utility.
	 * 
	 * @param gson the gson utility
	 */
	protected AbstractEncoder(Gson gson) {
		this.gson = gson;
	}

	@Override
    public String encode(T value) throws EncodeException {
    	try {
    		return gson.toJson(value);
    	}
    	catch (RuntimeException e) {
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