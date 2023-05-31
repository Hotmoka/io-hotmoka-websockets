package io.hotmoka.websockets.beans;

import java.util.function.Supplier;
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
	 * The type of the objects encoded by the encoder.
	 */
	private final Class<T> beanClass;

	/**
	 * The encoding utility.
	 */
	private final static Gson gson = new Gson();

	private final static Logger LOGGER = Logger.getLogger(BaseEncoder.class.getName());

	/**
	 * Creates an encoder for the given type.
	 * 
	 * @param beanClass the type of the objects encoded by the encoder
	 */
	protected BaseEncoder(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Yields the actual object that gets transformed into Json.
	 * Normally, this yields {@code null}, meaning that the object
	 * is not transformed before encoding. Subclasses may redefine.
	 * 
	 * @param value the value that must be transformed into Json
	 * @return the actual object that will get transformed into Json;
	 *         it is a supplier since it is able to compute the {@code value} back
	 */
	public Supplier<T> map(T value) {
		return null;
	}

	@Override
    public final String encode(T value) throws EncodeException {
		var json = map(value);
		var actual = json == null ? value : json;

		try {
    		return gson.toJson(actual);
    	}
    	catch (Exception e) {
    		LOGGER.log(Level.SEVERE, "could not encode a " + beanClass.getName(), e);
    		throw new EncodeException(value, "could not encode a " + beanClass.getName(), e);
    	}
    }
}