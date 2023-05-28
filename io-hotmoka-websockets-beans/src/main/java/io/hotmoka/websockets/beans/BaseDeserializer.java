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

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * A base implementation of a serializer into Json.
 *
 * @param <T> the type of the serialized objects
 */
public class BaseDeserializer<T> implements JsonDeserializer<T> {

	/**
	 * The class whose objects get deserialized.
	 */
	Class<T> beanClass;

	/**
	 * The utility used for converting from Json.
	 */
	final Gson gson;

	/**
	 * Builds the deserializer.
	 * 
	 * @param beanClass the type of the deserialized objects
	 */
	protected BaseDeserializer(Class<T> beanClass) {
		this.beanClass = beanClass;
		var builder = new GsonBuilder();
		registerTypeDeserializers(builder);
		this.gson = builder.create();
	}

	/**
	 * Called at creation-time, to enrich the conversion with specific deserializers
	 * for types used in the fields of {@code T}.
	 * 
	 * @param where the builder where specific type deserializers can be added, if needed
	 */
	protected void registerTypeDeserializers(GsonBuilder where) {
		// subclasses may redefine
	}

	/**
	 * Template method for the actual deserialization.
	 * 
	 * @param json the json to use for deserialization
	 * @param gson the utility that can be used for deserialization
	 * @return the deserialized object
	 * @throws JsonParseException if the object cannot be deserialized
	 */
	protected T deserialize(JsonElement json, Gson gson) throws JsonParseException {
		return gson.fromJson(json, beanClass);
	}

	/**
	 * Register this serializer as a type adapter for the class of the objects that it serializes.
	 * 
	 * @param where the Gson utility where it must be registered
	 */
	void registerAsTypeDeserializer(GsonBuilder where) {
		where.registerTypeAdapter(beanClass, this);
	}

	/**
	 * Template method for the actual deserialization.
	 * 
	 * @param json the json to use for deserialization
	 * @return the deserialized object
	 * @throws JsonParseException if the object cannot be deserialized
	 */
	T deserialize(JsonElement json) throws JsonParseException {
		return deserialize(json, gson);
	}

	@Override
	public final T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return deserialize(json, gson);
	}
}