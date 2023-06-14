/*
 * @(#) JSONMockServerDSL.kt
 *
 * kjson-spring3-test  Spring Boot 3 JSON testing functions for kjson
 * Copyright (c) 2022, 2023 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.kjson.spring.test

import java.net.URI

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.test.web.client.ResponseCreator

import io.kjson.JSONConfig
import io.kjson.stringifyJSON
import io.kjson.test.JSONExpect

/**
 * DSL class to configure mock requests to a [JSONMockServer] (wrapper class for Spring's `MockRestServiceServer`).
 *
 * @author  Peter Wall
 */
class JSONMockServerDSL(val config: JSONConfig): ResponseCreator {

    internal lateinit var request: MockClientHttpRequest

    internal var response: Response? = null
        set(newResponse) {
            if (field != null && newResponse != null)
                throw AssertionError("Response already set")
            field = newResponse
        }

    /**
     * Match the request URI using a string.
     */
    fun requestTo(expectedURI: String) {
        requestTo(URI(expectedURI))
    }

    /**
     * Match the URI against another URI.
     */
    fun requestTo(expectedURI: URI) {
        val uri = request.uri
        if (!uri.equalIgnoringQuery(expectedURI))
            fail("Request URI doesn't match; expected $expectedURI, was $uri")
    }

    /**
     * Match the URI using a lambda.
     */
    fun requestTo(test: (String) -> Boolean) {
        if (!test(request.uri.toString()))
            fail("Request URI doesn't match; was ${request.uri}")
    }

    /**
     * Match the URI using a [Matcher].
     */
    @Deprecated("The use of Matcher will be removed in a future version", ReplaceWith("requestTo { test(it) }"))
    fun requestTo(matcher: Matcher<in String>) {
        MatcherAssert.assertThat("Request URI", request.uri.toString(), matcher)
    }

    /**
     * Match the method.
     */
    fun method(method: HttpMethod) {
        if (request.method != method)
            fail("Request method incorrect; expected $method, was ${request.method}")
    }

    /**
     * Match a query parameter.  A query parameter with multiple values will be matched against a set of values.
     */
    fun queryParam(name: String, vararg expectedValues: String) {
        val queryMap = JSONMockClientRequest.decodeQueryParams(request.uri)
        val entries = queryMap[name]
        if (entries.isNullOrEmpty())
            throw AssertionError("Request query param [$name] not found")
        val n = expectedValues.size
        val s = entries.size
        if (s != n) {
            when (n) {
                1 -> fail("Request query param [$name] incorrect; expected single param, was multiple ($s)")
                else -> fail("Request query param [$name] number incorrect; expected $n, was $s")
            }
        }
        for (i in 0 until n) {
            if (expectedValues[i] != entries[i])
                fail("Request query param [$name] incorrect; expected ${expectedValues[i]}, was ${entries[i]}")
        }
    }

    /**
     * Match a query parameter using a lambda.
     */
    fun queryParam(name: String, test: (String) -> Boolean) {
        val queryMap = JSONMockClientRequest.decodeQueryParams(request.uri)
        val entries = queryMap[name]
        if (entries.isNullOrEmpty())
            throw AssertionError("Request query param [$name] not found")
        if (entries.size != 1)
            fail("Request query param [$name] incorrect; expected single param, was multiple (${entries.size})")
        val param = entries[0]
        if (param == null || !test(param))
            fail("Request query param [$name] incorrect")
    }

    /**
     * Match a request header.  A header with multiple values will be matched against a set of values.
     */
    fun header(name: String, vararg expectedValues: String) {
        val n = expectedValues.size
        val headers = getHeaders(name, n)
        for (i in 0 until n)
            if (expectedValues[i] != headers[i])
                fail("Request header [$name] incorrect; expected ${expectedValues[i]}, was ${headers[i]}")
    }

    /**
     * Match a request header using a lambda.
     */
    fun header(name: String, test: (String) -> Boolean) {
        val header = getHeaders(name, 1).first()
        if (!test(header))
            fail("Request header [$name] incorrect; was $header")
    }

    /**
     * Match a request header using a [Matcher].
     */
    @Deprecated("The use of Matcher will be removed in a future version", ReplaceWith("header(name) { test(it) }"))
    fun header(name: String, vararg matchers: Matcher<in String?>) {
        val n = matchers.size
        val headers = getHeaders(name, n)
        for (i in 0 until n)
            MatcherAssert.assertThat("Request header [$name]", headers[i], matchers[i])
    }

    /**
     * Match the `Accept` header against a specified [MediaType].  An `Accept` header with multiple values will be
     * considered to match if any of the entries is compatible with the expected type.
     */
    fun accept(expectedMediaType: MediaType) {
        val header = getHeaders(HttpHeaders.ACCEPT, 1).first()
        header.split(',').map { it.trim() }.forEach {
            if (checkMediaType(it, HttpHeaders.ACCEPT).isCompatibleWith(expectedMediaType))
                return
        }
        fail("Request [Accept] header incorrect; expected $expectedMediaType, was $header")
    }

    /**
     * Match the `Accept` header with `application/json`.
     */
    fun acceptApplicationJSON() {
        accept(MediaType.APPLICATION_JSON)
    }

    /**
     * Match the `Content-Type` header against a specified [MediaType].
     */
    fun contentType(expectedMediaType: MediaType) {
        val header = getHeaders(HttpHeaders.CONTENT_TYPE, 1).first()
        if (!checkMediaType(header, HttpHeaders.CONTENT_TYPE).isCompatibleWith(expectedMediaType))
            fail("Request [Content-Type] header incorrect; expected $expectedMediaType, was $header")
    }

    /**
     * Match the `Content-Type` header with `application/json`.
     */
    fun contentTypeApplicationJSON() {
        contentType(MediaType.APPLICATION_JSON)
    }

    private fun getHeaders(name: String, expectedCount: Int): List<String> {
        val headers = request.headers[name] ?: throw AssertionError("Header [$name] not found")
        if (headers.size != expectedCount) {
            when (expectedCount) {
                1 -> fail("Request [$name] header; expected single header, was multiple (${headers.size})")
                else -> fail("Request [$name] header number incorrect; expected $expectedCount, was ${headers.size}")
            }
        }
        return headers
    }

    /**
     * Test that the nominated request header is not present in the request.
     */
    fun headerDoesNotExist(name: String) {
        if (request.headers.containsKey(name))
            fail("Request [$name] header expected not to be present")
    }

    /**
     * Match the request body content against a string.
     */
    fun requestContent(body: String) {
        if (request.bodyAsString != body)
            fail("Request body incorrect")
    }

    /**
     * Match the request body content using a lambda.
     */
    fun requestContent(test: (String) -> Boolean) {
        if (!test(request.bodyAsString))
            fail("Request body incorrect")
    }

    /**
     * Match the request body using [JSONExpect] tests.
     */
    fun requestJSON(tests: JSONExpect.() -> Unit) {
        contentTypeApplicationJSON()
        JSONExpect.expectJSON(request.bodyAsString, tests)
    }

    /**
     * Specify the response from the request as an object to be serialised to JSON.  The object will be created
     * dynamically, and the lambda that creates it will have access to the request variables.
     */
    fun respondJSON(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        lambda: JSONMockClientRequest.() -> Any?
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, MediaType.APPLICATION_JSON),
            lambda = { lambda().stringifyJSON(config).toByteArray() },
        )
    }

    /**
     * Specify the response from the request as a fixed object to be serialised to JSON.
     */
    fun respondJSON(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        result: Any?,
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, MediaType.APPLICATION_JSON),
            body = result.stringifyJSON(config).toByteArray()
        )
    }

    /**
     * Specify the response from the request as a fixed string.
     */
    fun respond(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        contentType: MediaType? = null,
        result: String? = null,
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, contentType),
            body = result?.toByteArray(),
        )
    }

    /**
     * Specify the response from the request as a string.  The string will be created dynamically, and the lambda that
     * creates it will have access to the request variables.
     */
    fun respond(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        contentType: MediaType? = null,
        lambda: JSONMockClientRequest.() -> String?
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, contentType),
            lambda = { lambda()?.toByteArray() },
        )
    }

    /**
     * Specify the response from the request as a fixed string, with the type `text/plain`.
     */
    fun respondTextPlain(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        result: String? = null,
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, MediaType.TEXT_PLAIN),
            body = result?.toByteArray(),
        )
    }

    /**
     * Specify the response from the request as a string, with the type `text/plain`.  The string will be created
     * dynamically, and the lambda that creates it will have access to the request variables.
     */
    fun respondTextPlain(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        lambda: JSONMockClientRequest.() -> String?
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, MediaType.TEXT_PLAIN),
            lambda = { lambda()?.toByteArray() },
        )
    }

    /**
     * Specify the response from the request as a fixed byte array.
     */
    fun respondBytes(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        contentType: MediaType? = null,
        result: ByteArray? = null,
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, contentType),
            body = result,
        )
    }

    /**
     * Specify the response from the request as a fixed byte array.  The byte array will be created dynamically, and the
     * lambda that creates it will have access to the request variables.
     */
    fun respondBytes(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders? = null,
        contentType: MediaType? = null,
        lambda: JSONMockClientRequest.() -> ByteArray?
    ) {
        response = Response(
            config = config,
            status = status,
            headers = combineHeaders(headers, contentType),
            lambda = lambda,
        )
    }

    /**
     * Create the [ClientHttpResponse] for this request, **if** the response has been set.  If not, `null` will be
     * returned, allowing the response to be set using a chained `andRespond()` function.
     */
    @Suppress("WRONG_NULLABILITY_FOR_JAVA_OVERRIDE")
    override fun createResponse(request: ClientHttpRequest?): ClientHttpResponse? {
        return response?.let { resp ->
            val body = resp.getValue(request as MockClientHttpRequest)
            MockClientHttpResponse(body, resp.status).apply { resp.headers?.let { headers.addAll(it) } }
        }
    }

    class Response(
        val config: JSONConfig,
        val status: HttpStatus,
        val headers: HttpHeaders?,
        private val body: ByteArray? = null,
        private val lambda: (JSONMockClientRequest.() -> ByteArray?)? = null
    ) {

        init {
            if (body != null && lambda != null)
                throw RuntimeException("Response body and lambda may not both be specified")
        }

        fun getValue(mockClientHttpRequest: MockClientHttpRequest): ByteArray {
            body?.let { return  it }
            lambda?.let { return JSONMockClientRequest(mockClientHttpRequest, config).it() ?: ByteArray(0) }
            return ByteArray(0)
        }

    }

    companion object {

        fun fail(message: String): Nothing {
            throw AssertionError(message)
        }

        fun checkMediaType(header: String, name: String): MediaType = try {
            MediaType.parseMediaType(header)
        } catch (_: Exception) {
            fail("Request [$name] header media type invalid: $header")
        }

        fun combineHeaders(headers: HttpHeaders?, contentType: MediaType?): HttpHeaders? = when {
            contentType != null -> HttpHeaders().also { h ->
                headers?.let { h.addAll(it) }
                h.contentType = contentType
            }
            else -> headers
        }

        fun URI.equalIgnoringQuery(other: URI): Boolean = if (isOpaque)
            other.isOpaque && schemeSpecificPart == other.schemeSpecificPart
        else
            !other.isOpaque && scheme == other.scheme && userInfo == other.userInfo && host == other.host &&
                    port == other.port && path == other.path

    }

}
