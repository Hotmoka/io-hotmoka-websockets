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

package io.hotmoka.websockets.beans.api;

/**
 * A function that transforms a value into its JSON representation.
 *
 * @param <T> the type of the value
 * @param <JSON> the type of the JSON representation ofthe value
 */
public interface ToJsonRepresentation<T, JSON extends JsonRepresentation<T>> {

	/**
	 * Maps a value into its JSON representation.
	 * 
	 * @param value the value
	 * @return its JSON representation
	 * @throws Exception if the conversion failed
	 */
	JSON map(T value) throws Exception;
}