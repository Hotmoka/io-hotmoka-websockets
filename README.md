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
$ ./run_server.sh
```
Then in another shell:

```shell
$ ./run_client.sh Textor
```

(replace `Textor` with your name for a more personal feeling.)

<p align="center"><img width="100" src="https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by.png" alt="This documentation is licensed under a Creative Commons Attribution 4.0 Internat
ional License"></p><p align="center">This document is licensed under a Creative Commons Attribution 4.0 International License.</p>

<p align="center">Copyright 2023 by Fausto Spoto (fausto.spoto@hotmoka.io)</p>