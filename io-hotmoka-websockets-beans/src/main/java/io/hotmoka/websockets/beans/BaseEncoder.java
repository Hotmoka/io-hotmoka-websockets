package io.hotmoka.websockets.beans;

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
	 * @param beanClass the type of the objects encoder by this encoder
	 */
	protected BaseEncoder(Class<T> beanClass) {
		this(new BaseSerializer<>(beanClass));
	}

	@Override
    public final String encode(T value) throws EncodeException {
    	try {
    		return serializer.gson.toJson(value);
    	}
    	catch (Exception e) {
    		throw new EncodeException(value, "could not encode bean", e);
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