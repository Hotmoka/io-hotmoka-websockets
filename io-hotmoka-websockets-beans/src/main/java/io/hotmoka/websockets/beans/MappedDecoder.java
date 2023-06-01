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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.hotmoka.websockets.beans.api.DecoderText;
import jakarta.websocket.DecodeException;

/**
 * Base implementation of a mapped decoder from JSON strings into objects.
 * This decoder first maps back to object to decode into another object, that is actually
 * used for decoding.
 *
 * @param <T> the type of the object
 * @param <JSON> the type of the mapped object
 */
public class MappedDecoder<T, JSON extends JsonRepresentation<T>> implements DecoderText<T> {

	/**
	 * The type of the objects decoded by the decoder.
	 */
	private final Class<JSON> clazz;

	private final static Gson gson = new Gson();

	private final static Logger LOGGER = Logger.getLogger(MappedDecoder.class.getName());

	/**
	 * Creates a decoder for the given class type.
	 * 
	 * @param clazz the type of the objects decoded by the decoder
	 */
	public MappedDecoder(Class<JSON> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}

	@Override
	public final T decode(String s) throws DecodeException {
		try {
			return gson.fromJson(JsonParser.parseString(s), clazz).unmap();
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "could not decode a " + clazz.getName(), e);
			throw new DecodeException(s, "could not decode a " + clazz.getName(), e);
		}
	}
}