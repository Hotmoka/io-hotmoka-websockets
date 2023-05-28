package io.hotmoka.websockets.beans;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.hotmoka.websockets.beans.api.DecoderText;
import jakarta.websocket.DecodeException;
import jakarta.websocket.EndpointConfig;

/**
 * Base implementation of a decoder from JSON strings into objects.
 *
 * @param <T> the type of the objects
 */
public abstract class BaseDecoder<T> implements DecoderText<T> {

	private final static Logger LOGGER = Logger.getLogger(BaseDecoder.class.getName());

	/**
	 * The deserializer to use for decoding.
	 */
	protected final BaseDeserializer<T> deserializer;

	/**
	 * Creates a decoder from the given deserializer.
	 * 
	 * @param deserializer the deserializer
	 */
	protected BaseDecoder(BaseDeserializer<T> deserializer) {
		this.deserializer = deserializer;
	}

	/**
	 * Creates a decoder using a default deserializer.
	 * 
	 * @param beanClass the type of the objects decoded by this decoder
	 */
	protected BaseDecoder(Class<T> beanClass) {
		this(new BaseDeserializer<>(beanClass));
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}

	@Override
	public final T decode(String s) throws DecodeException {
		try {
			return decode(JsonParser.parseString(s), deserializer.gson);
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "could not decode a " + deserializer.beanClass.getName(), e);
			throw new DecodeException(s, "could not decode a " + deserializer.beanClass.getName(), e);
		}
	}

	/**
	 * Template method for actual decoding.
	 * 
	 * @param element the json to decode
	 * @param gson the utility that can be used for deserialization
	 * @return the decoded object
	 * @throws Exception if something fails during the decoding
	 */
	protected T decode(JsonElement element, Gson gson) throws Exception {
		return deserializer.deserialize(element);
	}

	/**
	 * Register this decoder as a deserializer for the objects decoded by itself.
	 * 
	 * @param where the Gson utility where it must be registered
	 */
	public final void registerAsTypeDeserializer(GsonBuilder where) {
		deserializer.registerAsTypeDeserializer(where);
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