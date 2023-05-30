package io.hotmoka.websockets.beans;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.hotmoka.websockets.beans.api.EncoderText;
import jakarta.websocket.EncodeException;
import jakarta.websocket.EndpointConfig;

/**
 * Base implementation of an encoder from an object into a JSON string.
 *
 * @param <T> the type of the object
 */
public class BaseEncoder<T> implements EncoderText<T> {

	private final static Logger LOGGER = Logger.getLogger(BaseEncoder.class.getName());

	/**
	 * The serializer to use for the encoding.
	 */
	protected final BaseSerializer<T> serializer;

	/**
	 * Creates an encoder from the given serializer.
	 * 
	 * @param serializer the serializer
	 */
	protected BaseEncoder(BaseSerializer<T> serializer) {
		this.serializer = serializer;
	}

	/**
	 * Creates an encoder using a default serializer.
	 * 
	 * @param beanClass the type of the objects encoded by this encoder
	 */
	protected BaseEncoder(Class<T> beanClass) {
		this(new BaseSerializer<>(beanClass));
	}

	/**
	 * Yields the utility to use for serialization.
	 * 
	 * @return the Gson utility
	 */
	protected final Gson getGson() {
		return serializer.gson;
	}

	@Override
    public final String encode(T value) throws EncodeException {
    	try {
    		return serializer.gson.toJson(value);
    	}
    	catch (Exception e) {
    		LOGGER.log(Level.SEVERE, "could not encode a " + serializer.beanClass.getName(), e);
    		throw new EncodeException(value, "could not encode a " + serializer.beanClass.getName(), e);
    	}
    }

	/**
	 * Register this encoder as a serializer for the objects encoded by itself.
	 * 
	 * @param where the Gson utility where it must be registered
	 */
	public final void registerAsTypeSerializer(GsonBuilder where) {
		serializer.registerAsTypeSerializer(where);
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