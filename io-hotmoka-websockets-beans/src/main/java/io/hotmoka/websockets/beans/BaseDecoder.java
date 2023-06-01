package io.hotmoka.websockets.beans;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.hotmoka.websockets.beans.api.DecoderText;
import jakarta.websocket.DecodeException;

/**
 * Base implementation of a decoder from JSON strings into objects.
 *
 * @param <T> the type of the objects
 */
public class BaseDecoder<T> implements DecoderText<T> {

	/**
	 * The type of the objects decoded by the decoder.
	 */
	private final Class<? extends T> clazz;

	private final static Gson gson = new Gson();

	private final static Logger LOGGER = Logger.getLogger(BaseDecoder.class.getName());

	/**
	 * Creates a decoder for the given class type.
	 * 
	 * @param clazz the type of the objects decoded by the decoder
	 */
	public BaseDecoder(Class<? extends T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}

	@Override
	public final T decode(String s) throws DecodeException {
		try {
			return gson.fromJson(JsonParser.parseString(s), clazz);
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "could not decode a " + clazz.getName(), e);
			throw new DecodeException(s, "could not decode a " + clazz.getName(), e);
		}
	}
}