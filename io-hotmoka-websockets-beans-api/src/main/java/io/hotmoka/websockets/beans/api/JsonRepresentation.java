/*
Copyright 2024 Fausto Spoto

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
 * A JSON representation of a value.
 *
 * @param <T> the type of the represented value
 * @param <D> the type of the decoder to use for this representation
 */
public interface JsonRepresentation<T, D extends DecoderText<T>> {

	/**
	 * Supplies the represented value.
	 * 
	 * @param decoder the decoder to use
	 * @return the represented value
	 * @throws InconsistentJsonException if the Json representation is inconsistent
	 * @throws Exception if the represented value cannot be supplied for a limit of the system
	 */
	T unmap(D decoder) throws InconsistentJsonException, Exception;
}