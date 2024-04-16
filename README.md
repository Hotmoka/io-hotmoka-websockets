[![Java-Build Action Status](https://github.com/Hotmoka/websockets/actions/workflows/java_build.yml/badge.svg)](https://github.com/Hotmoka/websockets/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.hotmoka.websockets/io-hotmoka-websockets-server.svg?label=Maven%20Central)](https://central.sonatype.com/search?smo=true&q=g:io.hotmoka.websockets)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Websockets in Java

This project is a minimal library for simplifying
the development of applications connected through websockets, in Java.

## Main features

1. Fully modularized
2. Stubborn division in API and implementation modules
3. Beans are immutable objects (all their fields are `final`) despite
   being managed through Gson
4. Bean types are interfaces
5. Endpoints hold a reference to their client or server, for instance-based
   state (no `static` singletons)

## Structure

The `io-hotmoka-websockets-*` modules are a minimal library for websockets,
split into `server`, `client` and `beans`
(objects marshalled and unmarshalled between clients and servers).
The `io-hotmoka-chat-*` modules are a minimal example that uses the library
to implement a chat: messages are exchanged between a client and a server.

## How to run it

Package the jars, run the server in a shell and the client in another shell:

```shell
$ mvn clean install
$ ./io-hotmoka-chat-server/run.sh
```
Then in another shell:

```shell
$ ./io-hotmoka-chat-client/run.sh Textor
```

(replace `Textor` with your name for a more personal feeling.)

## A note about the no-args constructor

This library uses [Gson](https://github.com/google/gson) to translate messages into strings and back. This means
that such message objects should have a public no-args constructor. See for instance
the `Json` inner class of [io.hotmoka.chat.beans.Message](https://github.com/Hotmoka/io-hotmoka-websockets/blob/main/io-hotmoka-chat-beans/src/main/java/io/hotmoka/chat/beans/Messages.java).
If you want to avoid that no-args constructor, then add the following to the
descriptor of the module that defines your messages:

```java
requires jdk.unsupported;
```

In this case, Gson will instantiate the messages through reflection and leave the missing
fields to their default value.

<p align="center"><img width="100" src="https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by.png" alt="This documentation is licensed under a Creative Commons Attribution 4.0 Internat
ional License"></p><p align="center">This document is licensed under a Creative Commons Attribution 4.0 International License.</p>

<p align="center">Copyright 2023 by Fausto Spoto (fausto.spoto@hotmoka.io)</p>