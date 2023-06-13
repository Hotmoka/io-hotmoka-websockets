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

import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

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

	private final static Logger LOGGER = Logger.getLogger(MappedEncoder.class.getName());

	/**
	 * The mapper from the object to their representation, that is actually encoded in JSON.
	 */
	private final Function<T, JSON> mapper;

	/**
	 * The name of the property used to state the type of the object, if required.
	 */
	private final String typePropertyName;

	/**
	 * The value of the type property, if required.
	 */
	private final String typePropertyValue;

	/**
	 * Creates an encoder for the given type mapper.
	 * 
	 * @param mapper the mapper from the object to their representation, that is actually encoded in JSON
	 */
	public MappedEncoder(Function<T, JSON> mapper) {
		this(mapper, "type", null);
	}

	/**
	 * Creates an encoder for the given type mapper. It adds a property named {@code type} bound
	 * to the given value.
	 * 
	 * @param mapper the mapper from the object to their representation, that is actually encoded in JSON
	 * @param typePropertyValue the value of the type property
	 */
	public MappedEncoder(Function<T, JSON> mapper, String typePropertyValue) {
		this(mapper, "type", typePropertyValue);
	}

	/**
	 * Creates an encoder for the given type mapper. It adds a property named {@code type} bound
	 * to the fully-qualified name of the given class.
	 * 
	 * @param mapper the mapper from the object to their representation, that is actually encoded in JSON
	 * @param typePropertyClass the class whose fully-qualified name is used as type property value
	 */
	public MappedEncoder(Function<T, JSON> mapper, Class<? extends T> typePropertyClass) {
		this(mapper, "type", typePropertyClass.getName());
	}

	/**
	 * Creates an encoder for the given type mapper. It adds a property for the type of the object, bound to the given value.
	 * 
	 * @param mapper the mapper from the object to their representation, that is actually encoded in JSON
	 * @param typePropertyName the name of the property used to state the type of the object
	 * @param typePropertyValue the value of the type property
	 */
	public MappedEncoder(Function<T, JSON> mapper, String typePropertyName, String typePropertyValue) {
		Objects.requireNonNull(mapper);
		Objects.requireNonNull(typePropertyName);
		this.mapper = mapper;
		this.typePropertyName = typePropertyName;
		this.typePropertyValue = typePropertyValue;
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
			JsonElement jsonTree = gson.toJsonTree(map(value));
			if (typePropertyValue != null && jsonTree.isJsonObject())
				jsonTree.getAsJsonObject().addProperty(typePropertyName, typePropertyValue);

			return jsonTree.toString();
    	}
    	catch (RuntimeException e) {
    		String type = value == null ? "null" : ("a " + value.getClass().getName());
    		LOGGER.log(Level.SEVERE, "could not encode " + type, e);
    		throw new EncodeException(value, "could not encode " + type, e);
    	}
    }
}