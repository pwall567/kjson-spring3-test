# kjson-spring3-test

[![Build Status](https://travis-ci.com/pwall567/kjson-spring3-test.svg?branch=main)](https://travis-ci.com/github/pwall567/kjson-spring3-test)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/static/v1?label=Kotlin&message=v1.7.21&color=7f52ff&logo=kotlin&logoColor=7f52ff)](https://github.com/JetBrains/kotlin/releases/tag/v1.7.21)
[![Maven Central](https://img.shields.io/maven-central/v/io.kjson/kjson-spring3-test?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.kjson%22%20AND%20a:%22kjson-spring3-test%22)

Spring Boot 3 JSON testing functions for [`kjson`](https://github.com/pwall567/kjson).

## **IMPORTANT**

**Version 4.4 is a major re-work of this library, and it will be a breaking change for users of earlier versions.**

Prior to version 4.4, the library used a flawed technique for acquiring the `JSONConfig` used by the application.
Sometimes it would work, but many times it wouldn&rsquo;t.

**Version 4.4.3 is a further revision of these changes**; the `JSONMockMvc` class introduced with the earlier changes
has been dropped in favour of a set of extension functions on `MockMvc`.

## Background

The [Spring Framework](https://spring.io/projects/spring-framework) provides a number of classes to assist with testing;
the `kjson-spring-test` library adds functionality to simplify the use of these tests in conjunction with the
[`kjson`](https://github.com/pwall567/kjson) library.

This library is a copy of the [`kjson-spring-test`](https://github.com/pwall567/kjson-spring-test) library, converted to
Spring Boot 3.x and Spring 6.x.

## Incoming Requests and `MockMvc`

The testing of incoming REST requests involves setting up incoming calls, and then testing the result to confirm that
it matches the expected status, data _etc._

The `kjson-spring-test` library can help with both of these aspects of incoming request testing, including the use of
the `kjson-test` library for testing / matching the response.

### `getForJSON`, `postForJSON`

The Kotlin extensions for Spring added `get`, `post` functions _etc._ to `MockMvc`, each function taking a lambda using
a DSL to configure the operation.
The `kjson-spring-test` library adds `getForJSON`, `putForJSON`, `postForJSON` and `deleteForJSON`, which set the
`Accept` header to `application/json` to indicate that the expected response is JSON, along with `putJSON` and
`postJSON` which do not set the `Accept` header but still allow JSON request content.

For example:
```kotlin
        mockMvc.getForJSON("/testendpoint") {
            header("X-Custom-Header", "value")
        }.andExpect {
            // check response
        }
```

The functions use a DSL broadly similar to that used by the existing `get` and `post` functions, as shown in the example
above, which uses the `header` function from that DSL.
A major difference is in the handling of the `content` property of the DSL &ndash; if it is set to a `String` or
`ByteArray` value it will be used as is, but any other type of object will be serialized using the `kjson` library, and
JSON form will be sent as the content (with the `Content-Type` set to `application/json`, unless already set to a
different value).

### `contentJSON`

To set the JSON content explicitly as JSON, the `contentJSON` function may be used:
```kotlin
        mockMvc.postForJSON("/testendpoint") {
            contentJSON {
                RequestData(
                    id = customerId,
                    name = customerName,
                )
            }
        }.andExpect {
            // check response
        }
```
There are two forms of the function, one which takes an object to be serialised and another (shown above) that takes a
lambda which will be invoked to create the object.

The `kjson` serialization will use the autowired `JSONConfig` configuration as described [below](#configuration).

### `matchesJSON`, `contentMatchesJSON`

The results of the `MockMvc` extension functions may be tested using the
[`kjson-test`](https://github.com/pwall567/kjson-test) library.

The Spring Kotlin extensions include the `content` function, which allows the specification of tests against the content
of the result.
`kjson-spring-test` adds the `matchesJSON` function within the content DSL;
this function parses the result as JSON, and then executes the `kjson-test` test specifications against the parsed
result.
For example:
```kotlin
        mockMvc.getForJSON("/testendpoint") {
            header("X-Custom-Header", "value")
        }.andExpect {
            status { isOk() }
            content {
                matchesJSON {
                    property("date", LocalDate.of(2022, 7, 6))
                    property("extra", "ResultValue")
                }
            }
        }
```

Alternatively, if the only function inside `content` is `matchesJSON`, the two may be combined:
```kotlin
        mockMvc.getForJSON("/testendpoint") {
            header("X-Custom-Header", "value")
        }.andExpect {
            status { isOk() }
            contentMatchesJSON {
                property("date", LocalDate.of(2022, 7, 6))
                property("extra", "ResultValue")
            }
        }
```

See the documentation for [`kjson-test`](https://github.com/pwall567/kjson-test) for more details on the matching
capabilities available using that library.

## Outgoing Requests and `JSONMockServer`

The testing of outgoing client REST requests is in some ways the reverse of incoming request testing.
The mock server is configured to match one or more possible requests, and to respond with the appropriate data.

Where the input request testing allows the use of the `kjson-test` library to test the result data, client request
testing allows the use of the same library for matching the requests (if complex matching is required).

In order to have access to the `kjson` serialization and deserialization functions, `kjson-spring-test` uses a wrapper
class around `MockRestServiceServer` named `JSONMockServer`.

### Creating a `JSONMockServer`

Instead of a Java static function, as in the case of `MockRestServiceServer`, the `JSONMockServer` must be obtained from
the `JSONSpringTest` service, as follows:
```kotlin
    @Autowired lateinit var jsonSpringTest: JSONSpringTest
```
Then, in the code (either in the test function or in a `@BeforeTest` function):
```kotlin
        val mockServer = jsonSpringTest.createServer(restTemplate)
```

### `mock`, `mockGet`, `mockPost`

`MockRestServiceServer` provides facilities for testing REST clients, but the conventional use of this class involves a
chain of "fluent" function calls, using `MockRestRequestMatchers` static functions.
`JSONMockServer` provides a DSL-based approach to client testing.

The `mock` function allows mock requests to be declared in a Kotlin idiomatic
manner:
```kotlin
    mockServer.mock {
        requestTo("/endpoint")
        method(HttpMethod.GET)
        header("X-Custom-Header", "value")
    }
```

The parameters for the `mock` function are:

| Name            | Type            | Default                | Description                                               |
|-----------------|-----------------|------------------------|-----------------------------------------------------------|
| `expectedCount` | `ExpectedCount` | `ExpectedCount.once()` | The number of times the request is expected to be invoked |
| `method`        | `HttpMethod`    | `HttpMethod.GET`       | The expected method                                       |
| `uri`           | `URI`           | none                   | The expected URI                                          |
| `block`         | lambda          | none                   | The DSL lambda (see below)                                |

There are also `mockGet`, `mockPut`, `mockPost` and `mockDelete` functions; these are convenience functions that avoid
the need to specify the method separately (although as noted above, `GET` is the default).
They do not take a `method` parameter (obviously).

Many of the functions within the `mock` lambda are named identically to the `MockRestRequestMatchers` static functions,
but in this case they are functions in the DSL created by the `mock` function.
There are also additional functions related to the use of JSON.

The following functions are available:

| Name                         | Parameter(s)                               | Description                                                             |
|------------------------------|--------------------------------------------|-------------------------------------------------------------------------|
| `requestTo`                  | `String`                                   | Matches the URI by string                                               |
| `requestTo`                  | `URI`                                      | Matches the URI                                                         |
| `requestTo`                  | `(String) -> Boolean`                      | Matches the URI using a lambda                                          |
| `requestTo`                  | `Matcher<String>`                          | Matches the URI using a `Matcher`                                       |
| `method`                     | `HttpMethod`                               | Matches the method                                                      |
| `queryParam`                 | `String`, `vararg String`                  | Matches a named query parameter against a set of values                 |
| `header`                     | `String`, `vararg String`                  | Matches a named header against a set of values                          |
| `header`                     | `String`, `(String) -> Boolean`            | Matches a named header using a lambda                                   |
| `header`                     | `String`, `vararg Matcher<String>`         | Matches a named header against a set of values using `Matcher`s         |
| `accept`                     | `MediaType`                                | Matches the `Accept` header with the specified `MediaType`              |
| `acceptApplicationJSON`      |                                            | Matches the `Accept` header as compatible with `application/json`       |
| `contentType`                | `MediaType`                                | Matches the `Content-Type` header with the specified `MediaType`        |
| `contentTypeApplicationJSON` |                                            | Matches the `Content-Type` header as compatible with `application/json` |
| `headerDoesNotExist`         | `String`                                   | Expects the named header to not be present                              |
| `requestContent`             | `String`                                   | Matches the request body against a `String`                             |
| `requestContent`             | `(String) -> Boolean`                      | Matches the request body using a lambda                                 |
| `requestJSON`                | lambda - see below                         | Matches the request body using the `kjson-test` library                 |
| `respond`                    | `HttpStatus`, `HttpHeaders?`, `String`     | Supplies the status, headers and a result string to send as a response  |
| `respondJSON`                | `HttpStatus`, `HttpHeaders?`, `Any?`       | As above, but the result object is converted to JSON for output         |
| `respondJSON`                | `HttpStatus`, `HttpHeaders?`, `() -> Any?` | As above, but the lambda is called to create the result object          |

The `requestJSON` function allows the request body to be matched using the
[`kjson-test`](https://github.com/pwall567/kjson-test) library.
For example:
```kotlin
    mockServer.mock {
        requestTo("/endpoint")
        method(HttpMethod.POST)
        requestJSON {
            property("id", isUUID)
            property("name", length(1..99))
        }
    }
```
See the documentation for [`kjson-test`](https://github.com/pwall567/kjson-test) for more details on the matching
capabilities available using that library.

The `respond` and `respondJSON` functions allow the specification of the mock response.
In the case of `respond`, the result may be supplied as a `String`, or may be omitted, as might be appropriate for
status code 204 (No Content).
```kotlin
    mockServer.mock {
        requestTo("/endpoint")
        method(HttpMethod.GET)
        respond(HttpStatus.NO_CONTENT)
    }
```

The full set of parameters for `respond` is:

| Name      | Type          | Default          | Description                                       |
|-----------|---------------|------------------|---------------------------------------------------|
| `status`  | `HttpStatus`  | `HttpStatus.GET` | The status to be returned                         |
| `headers` | `HttpHeaders` | empty list       | The headers to be added to the response           |
| `result`  | `String?`     | `null`           | The result as a `String` (`null` means no result) |

The `respondJSON` function allows the specification of a result object that will be serialised to JSON, and in the case
of the form that takes a lambda parameter, the `JSONMockClientRequest` describing the request will be supplied as the
receiver object, allowing the use of data from the request in the creation of the response:
```kotlin
    mockServer.mock {
        requestTo { it.startsWith("/testendpoint/") }
        method(HttpMethod.GET)
        respondJSON {
            ResponseData(LocalDate.now(), uri.path.substringAfterLast('/'))
        }
    }
```

The `kjson` serialization will use the `JSONConfig` configuration as described [below](#configuration), and that
`JSONConfig` is available within the lambda as the `config` property of the `JSONMockClientRequest`.

The full set of parameters for `respondJSON` is:

| Name      | Type          | Default          | Description                              |
|-----------|---------------|------------------|------------------------------------------|
| `status`  | `HttpStatus`  | `HttpStatus.GET` | The status to be returned                |
| `headers` | `HttpHeaders` | empty list       | The headers to be added to the response  |
| `block`   | lambda        | none             | The lambda to create the response object |

### `JSONResponseActions.respondJSON`

As an alternative to the use of the `respondJSON` function within the request configuration block, the library also
provides a `respondJSON` function in the `JSONResponseActions` returned by the `mock` functions, and this may be chained
onto those functions.
This form of the `respondJSON` function takes the same parameters as the one described above, but it differs in that the
lambda that creates the response object does not have access to the `JSONMockClientRequest`.

To configure the mock operation to respond with a JSON object:
```kotlin
    mockServer.mockGet {
        requestTo("/endpoint")
    }.respondJSON {
        ResponseData(date = LocalDate.now(), extra = "XYZ")
    }
```

### `verify`

The `verify` functions of `MockRestServiceServer` are also available in `JSONMockServer`:
```kotlin
        mockServer.verify()
```

## Configuration

The `kjson` serialization and deserialization functions all take an optional
[`JSONConfig`](https://github.com/pwall567/kjson/blob/main/USERGUIDE.md#configuration) object.
The `JSONConfig` to be used by the functions invoked by the `kjson-spring-test` library may be provided in the usual
Spring manner:
```kotlin
@Configuration
open class SpringAppConfig {

    @Bean open fun config(): JSONConfig {
        return JSONConfig {
            // configuration options here
        }
    }

}
```

If the project is also using the [`kjson-spring3`](https://github.com/pwall567/kjson-spring3) library, the same
configuration may be shared by both libraries.

## Dependency Specification

The latest version of the library is 6.0 (the version number of this library matches the version of `kjson` with which
it was built), and it may be obtained from the Maven Central repository.
(The following dependency declarations assume that the library will be included for test purposes; this is
expected to be its principal use.)

This version was built using version 6.0.2 of Spring, and version 3.0.2 of Spring Boot.

### Maven
```xml
    <dependency>
      <groupId>io.kjson</groupId>
      <artifactId>kjson-spring3-test</artifactId>
      <version>6.0</version>
      <scope>test</scope>
    </dependency>
```
### Gradle
```groovy
    testImplementation 'io.kjson:kjson-spring3-test:6.0'
```
### Gradle (kts)
```kotlin
    testImplementation("io.kjson:kjson-spring3-test:6.0")
```

Peter Wall

2023-07-10
