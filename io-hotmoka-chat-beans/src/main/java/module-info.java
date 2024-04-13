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

/**
 * This module implements the beans exchanged among chat clients and servers.
 */
module io.hotmoka.chat.beans {
	exports io.hotmoka.chat.beans;
	// beans must be encoded and decoded by reflection through Gson
	opens io.hotmoka.chat.beans.internal to com.google.gson;

	requires transitive io.hotmoka.chat.beans.api;
	requires io.hotmoka.websockets.beans;

	// this would make sun.misc.Unsafe accessible, so that Gson can instantiate
	// classes without the no-args constructor; in this example, we prefer to add
	// a no-args constructor to the beans instead
	//requires jdk.unsupported;
}