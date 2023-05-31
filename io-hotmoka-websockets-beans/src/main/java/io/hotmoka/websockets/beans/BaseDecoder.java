package io.hotmoka.websockets.beans;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.hotmoka.websockets.beans.api.DecoderText;
import jakarta.websocket.DecodeException;

/**
 * Base implementation of a decoder from JSON strings into objects.
 *
 * @param <T> the type of the objects
 */
public abstract class BaseDecoder<T> implements DecoderText<T> {

	/**
	 * The type of the objects decoded by the decoder.
	 */
	private final Class<? extends T> beanClass;

	/**
	 * The type of the objects actually matched with Gson. If {@code null},
	 * then {@code beanClass} is used.
	 */
	private final Class<? extends Supplier<T>> jsonClass;

	private final static Gson gson = new Gson();

	private final static Logger LOGGER = Logger.getLogger(BaseDecoder.class.getName());

	/**
	 * Creates a decoder for the given class type.
	 * 
	 * @param beanClass the type of the objects decoded by the decoder
	 */
	protected BaseDecoder(Class<? extends T> beanClass) {
		this.beanClass = beanClass;
		this.jsonClass = null;
	}

	/**
	 * Creates a decoder for the given class type.
	 * 
	 * @param beanClass the type of the objects decoded by the decoder; this is not used
	 * @param jsonClass the type of the objects actually matched with Gson
	 */
	protected BaseDecoder(Class<? extends T> beanClass, Class<? extends Supplier<T>> jsonClass) {
		this.beanClass = null;
		this.jsonClass = jsonClass;
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}

	@Override
	public final T decode(String s) throws DecodeException {
		try {
			return decode(JsonParser.parseString(s), gson);
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "could not decode with a " + getClass().getName(), e);
			throw new DecodeException(s, "could not decode with a " + getClass().getName(), e);
		}
	}

	/**
	 * Decode the given json element by using the given gson utility.
	 * 
	 * @param json the element
	 * @param gson the utility
	 * @return the decoded value
	 */
	private T decode(JsonElement json, Gson gson) {
		if (jsonClass == null)
			return gson.fromJson(json, beanClass);
		else
			return gson.fromJson(json, jsonClass).get();
	}
}