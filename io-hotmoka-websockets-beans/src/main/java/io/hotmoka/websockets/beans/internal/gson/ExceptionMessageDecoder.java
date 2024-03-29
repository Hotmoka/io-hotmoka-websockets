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

package io.hotmoka.websockets.beans.internal.gson;

import io.hotmoka.websockets.beans.ExceptionMessages;
import io.hotmoka.websockets.beans.MappedDecoder;
import io.hotmoka.websockets.beans.api.ExceptionMessage;

/**
 * A decoder for {@link ExceptionMessage}.
 */
public class ExceptionMessageDecoder extends MappedDecoder<ExceptionMessage, ExceptionMessages.Json> {

	public ExceptionMessageDecoder() {
		super(ExceptionMessages.Json.class);
	}
}