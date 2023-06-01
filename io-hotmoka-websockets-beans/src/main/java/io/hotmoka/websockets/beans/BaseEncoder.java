package io.hotmoka.websockets.beans;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import io.hotmoka.websockets.beans.api.EncoderText;
import jakarta.websocket.EncodeException;

/**
 * Base implementation of an encoder from an object into a JSON string.
 *
 * @param <T> the type of the object
 */
public class BaseEncoder<T> implements EncoderText<T> {

	/**
	 * The encoding utility.
	 */
	private final static Gson gson = new Gson();

	private final static Logger LOGGER = Logger.getLogger(BaseEncoder.class.getName());

	@Override
    public final String encode(T value) throws EncodeException {
		try {
    		return gson.toJson(value);
    	}
    	catch (RuntimeException e) {
    		String type = value == null ? "null" : ("a " + value.getClass().getName());
    		LOGGER.log(Level.SEVERE, "could not encode " + type, e);
    		throw new EncodeException(value, "could not encode " + type, e);
    	}
    }
}