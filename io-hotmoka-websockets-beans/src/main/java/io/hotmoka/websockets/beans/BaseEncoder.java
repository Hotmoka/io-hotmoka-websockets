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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

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

	/**
	 * The name of the property used to state the type of the object, if required.
	 */
	private final String typePropertyName;

	/**
	 * The value of the type property, if required.
	 */
	private final String typePropertyValue;

	private final static Logger LOGGER = Logger.getLogger(BaseEncoder.class.getName());

	/**
	 * Creates an encoder.
	 */
	public BaseEncoder() {
		this("type", null);
	}

	/**
	 * Creates an encoder. It adds a property named {@code type} bound to the given value.
	 * 
	 * @param typePropertyValue the value of the type property
	 */
	public BaseEncoder(String typePropertyValue) {
		this("type", typePropertyValue);
	}

	/**
	 * Creates an encoder. It adds a property named {@code type} bound
	 * to the fully-qualified name of the given class.
	 * 
	 * @param typePropertyClass the class whose fully-qualified name is used as type property value
	 */
	public BaseEncoder(Class<? extends T> typePropertyClass) {
		this("type", typePropertyClass.getName());
	}

	/**
	 * Creates an encoder. It adds a property for the type of the object, bound to the given value.
	 * 
	 * @param typePropertyName the name of the property used to state the type of the object
	 * @param typePropertyValue the value of the type property
	 */
	public BaseEncoder(String typePropertyName, String typePropertyValue) {
		Objects.requireNonNull(typePropertyName);
		this.typePropertyName = typePropertyName;
		this.typePropertyValue = typePropertyValue;
	}

	@Override
    public final String encode(T value) throws EncodeException {
		try {
			JsonElement jsonTree = gson.toJsonTree(value);
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