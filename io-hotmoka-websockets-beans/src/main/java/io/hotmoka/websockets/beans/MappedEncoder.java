/*
Copyright 2023 Fausto Spoto

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.hotmoka.websockets.beans;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import io.hotmoka.websockets.beans.api.EncoderText;
import jakarta.websocket.EncodeException;

/**
 * Base implementation of a mapped encoder from an object into a JSON string.
 * This encoder first maps to object to encode into another object, that is actually
 * used for encoding.
 *
 * @param <T> the type of the object
 * @param <JSON> the type of the mapped object
 */
public class MappedEncoder<T, JSON extends JsonRepresentation<T>> implements EncoderText<T> {

	/**
	 * The encoding utility.
	 */
	private final static Gson gson = new Gson();

	private final static Logger LOGGER = Logger.getLogger(BaseEncoder.class.getName());

	/**
	 * The mapper from the object to their representation, that is actually encoded in JSON.
	 */
	private final Function<T, JSON> mapper;

	/**
	 * Creates an encoder for the given type.
	 * 
	 * @param mapper the mapper from the object to their representation, that is actually encoded in JSON
	 */
	public MappedEncoder(Function<T, JSON> mapper) {
		this.mapper = mapper;
	}

	/**
	 * Yields the actual object that gets transformed into Json.
	 * 
	 * @param value the value that must be transformed into Json
	 * @return the actual object that will get transformed into Json instead of {@code value};
	 *         it is a supplier since it is able to compute {@code value} back
	 */
	public final JSON map(T value) {
		return mapper.apply(value);
	}

	@Override
    public final String encode(T value) throws EncodeException {
		try {
    		return gson.toJson(map(value));
    	}
    	catch (RuntimeException e) {
    		String type = value == null ? "null" : ("a " + value.getClass().getName());
    		LOGGER.log(Level.SEVERE, "could not encode " + type, e);
    		throw new EncodeException(value, "could not encode " + type, e);
    	}
    }
}