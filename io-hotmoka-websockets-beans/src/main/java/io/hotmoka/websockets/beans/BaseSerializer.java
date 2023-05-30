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
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A base implementation of a serializer into Json.
 *
 * @param <T> the type of the serialized objects
 */
public class BaseSerializer<T> implements JsonSerializer<T> {

	/**
	 * The class whose objects get serialized.
	 */
	protected final Class<T> beanClass;

	/**
	 * The utility used for converting into Json.
	 */
	protected final Gson gson;

	/**
	 * Builds the serializer.
	 * 
	 * @param beanClass the type of the serialized objects
	 */
	protected BaseSerializer(Class<T> beanClass) {
		this.beanClass = beanClass;
		var builder = new GsonBuilder();
		registerTypeSerializers(builder);
		this.gson = builder.create();
	}

	/**
	 * Called at creation-time, to enrich the conversion with specific serializers
	 * for types used in the fields of {@code T}.
	 * 
	 * @param where the builder where specific type serializers can be added, if needed
	 */
	protected void registerTypeSerializers(GsonBuilder where) {
		// subclasses may redefine
	}

	/**
	 * Register this serializer as a type adapter for the class of the objects that it serializes.
	 * 
	 * @param where the Gson utility where it must be registered
	 */
	void registerAsTypeSerializer(GsonBuilder where) {
		where.registerTypeAdapter(beanClass, this);
	}

	@Override
	public final JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		// TODO: this is never used!
		return gson.toJsonTree(src);
	}
}