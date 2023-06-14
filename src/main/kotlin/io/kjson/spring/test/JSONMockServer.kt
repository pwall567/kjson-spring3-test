/*
 * @(#) JSONMockServer.kt
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
import java.time.Duration

import org.springframework.http.HttpMethod
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.RequestMatcher

import io.kjson.JSONConfig

/**
 * `JSONMockServer` is a wrapper class for Spring's [MockRestServiceServer], providing access to the [JSONConfig]
 * instance discovered through Spring auto-wiring.
 *
 * @author  Peter Wall
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class JSONMockServer(val mockRestServiceServer: MockRestServiceServer, val config: JSONConfig) {

    fun expect(requestMatcher: RequestMatcher): JSONResponseActions {
        return JSONResponseActions(mockRestServiceServer.expect(requestMatcher), config)
    }

    fun expect(expectedCount: ExpectedCount, requestMatcher: RequestMatcher): JSONResponseActions {
        return JSONResponseActions(mockRestServiceServer.expect(expectedCount, requestMatcher), config)
    }

    fun verify() {
        mockRestServiceServer.verify()
    }

    fun verify(timeout: Duration) {
        mockRestServiceServer.verify(timeout)
    }

    fun reset() {
        mockRestServiceServer.reset()
    }

    fun mock(
        expectedCount: ExpectedCount = ExpectedCount.once(),
        method: HttpMethod? = null,
        uri: URI? = null,
        block: JSONMockServerDSL.() -> Unit = {}
    ): JSONResponseActions {
        val serverDSL = JSONMockServerDSL(config)
        val responseActions = mockRestServiceServer.expect(expectedCount) { request ->
            serverDSL.apply {
                this.request = request as MockClientHttpRequest
                response = null
                method?.let { method(it) }
                uri?.let { requestTo(it) }
                block()
            }
        }
        responseActions.andRespond(serverDSL)
        // NOTE:
        // This works only because Spring does not complain when createResponse() returns null (see JSONMockServerDSL)
        // If that ever changes, we may need to enforce the use of the respondJSON and respond functions in this
        // class and remove the ability to use a chained andRespond() on the result of mock()
        return JSONResponseActions(responseActions, config)
    }

    /**
     * Establish a mock request with the method preset to GET.
     */
    fun mockGet(
        expectedCount: ExpectedCount = ExpectedCount.once(),
        uri: URI? = null,
        block: JSONMockServerDSL.() -> Unit = {}
    ) = mock(expectedCount, HttpMethod.GET, uri, block)

    /**
     * Establish a mock request with the method preset to POST.
     */
    fun mockPost(
        expectedCount: ExpectedCount = ExpectedCount.once(),
        uri: URI? = null,
        block: JSONMockServerDSL.() -> Unit = {}
    ) = mock(expectedCount, HttpMethod.POST, uri, block)

    /**
     * Establish a mock request with the method preset to PUT.
     */
    fun mockPut(
        expectedCount: ExpectedCount = ExpectedCount.once(),
        uri: URI? = null,
        block: JSONMockServerDSL.() -> Unit = {}
    ) = mock(expectedCount, HttpMethod.PUT, uri, block)

    /**
     * Establish a mock request with the method preset to DELETE.
     */
    fun mockDelete(
        expectedCount: ExpectedCount = ExpectedCount.once(),
        uri: URI? = null,
        block: JSONMockServerDSL.() -> Unit = {}
    ) = mock(expectedCount, HttpMethod.DELETE, uri, block)

    /**
     * Establish a mock request with the method preset to PATCH.
     */
    fun mockPatch(
        expectedCount: ExpectedCount = ExpectedCount.once(),
        uri: URI? = null,
        block: JSONMockServerDSL.() -> Unit = {}
    ) = mock(expectedCount, HttpMethod.PATCH, uri, block)

}
