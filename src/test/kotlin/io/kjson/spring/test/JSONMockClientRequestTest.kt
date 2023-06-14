/*
 * @(#) JSONMockClientRequestTest.kt
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

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.expect

import java.net.URI

import org.springframework.http.HttpMethod
import org.springframework.mock.http.client.MockClientHttpRequest

import io.kjson.JSON.asInt
import io.kjson.JSONConfig
import io.kjson.JSONObject

class JSONMockClientRequestTest {

    @Test fun `should return method`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        expect(HttpMethod.GET) { jmcr.method }
    }

    @Test fun `should return URI`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        expect(uri) { jmcr.uri }
    }

    @Test fun `should return body as string`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        mockClientHttpRequest.body.writer().use {
            it.append("DATA!")
        }
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        expect("DATA!") { jmcr.bodyAsString }
    }

    @Test fun `should return body as byte array`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        mockClientHttpRequest.body.writer().use {
            it.append("DATA!")
        }
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        assertTrue("DATA!".toByteArray().contentEquals(jmcr.bodyAsBytes))
    }

    @Test fun `should return body as JSON`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        mockClientHttpRequest.body.writer().use {
            it.append("""{"aaa":987}""")
        }
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        val json = jmcr.bodyAsJSON
        assertTrue(json is JSONObject)
        expect(987) { json["aaa"].asInt }
    }

    @Test fun `should return map of query parameters`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        val params = jmcr.paramsMap
        expect(2) { params.size }
        expect(2) { params["abc"]?.size }
        expect("123") { params["abc"]?.get(0) }
        expect("789") { params["abc"]?.get(1) }
        expect(1) { params["xyz"]?.size }
        expect("456") { params["xyz"]?.get(0) }
        assertNull(params["mmm"])
    }

    @Test fun `should return all headers`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        val headers = jmcr.headers
        expect(2) { headers.size }
        expect(2) { headers["X-Header-1"]?.size }
        expect("value1") { headers["X-Header-1"]?.get(0) }
        expect("value9") { headers["X-Header-1"]?.get(1) }
        expect(1) { headers["X-Header-2"]?.size }
        expect("value2") { headers["X-Header-2"]?.get(0) }
        assertNull(headers["X-Header-9"])
    }

    @Test fun `should return specific headers`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        val headers1 = jmcr.getHeaders("X-Header-1")
        assertNotNull(headers1)
        expect(2) { headers1.size }
        expect("value1") { headers1[0] }
        expect("value9") { headers1[1] }
        val headers2 = jmcr.getHeaders("X-Header-2")
        assertNotNull(headers2)
        expect(1) { headers2.size }
        expect("value2") { headers2[0] }
        assertNull(jmcr.getHeaders("X-Header-3"))
    }

    @Test fun `should return individual header`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        expect("value2") { jmcr.getHeader("X-Header-2") }
        assertNull(jmcr.getHeader("X-Header-3"))
    }

    @Test fun `should throw exception on get individual header when multiple present`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        assertFailsWith<AssertionError> { jmcr.getHeader("X-Header-1") }.let {
            expect("Request [X-Header-1] header - multiple headers (2)") { it.message }
        }
    }

    @Test fun `should report headers present`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        assertTrue(jmcr.hasHeader("X-Header-1"))
        assertTrue(jmcr.hasHeader("X-Header-2"))
        assertFalse(jmcr.hasHeader("X-Header-3"))
    }

    @Test fun `should return specific query parameters`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        val abc = jmcr.getParams("abc")
        assertNotNull(abc)
        expect(2) { abc.size }
        expect("123") { abc[0] }
        expect("789") { abc[1] }
        val xyz = jmcr.getParams("xyz")
        assertNotNull(xyz)
        expect(1) { xyz.size }
        expect("456") { xyz[0] }
        assertNull(jmcr.getParams("pqr"))
    }

    @Test fun `should return individual query parameter`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        expect("456") { jmcr.getParam("xyz") }
        assertNull(jmcr.getParam("qqq"))
    }

    @Test fun `should throw exception on get individual query parameter when multiple present`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        assertFailsWith<AssertionError> { jmcr.getParam("abc") }.let {
            expect("Request [abc] param - multiple params (2)") { it.message }
        }
    }

    @Test fun `should report query parameters present`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        assertTrue(jmcr.hasParam("abc"))
        assertTrue(jmcr.hasParam("xyz"))
        assertFalse(jmcr.hasParam("aaa"))
    }

    companion object {

        val config: JSONConfig = JSONConfig.defaultConfig

        val uri = URI("https://example.com/endpoint?abc=123&xyz=456&abc=789")

        val requestWithParams = MockClientHttpRequest(HttpMethod.GET, uri)

        val requestWithHeaders = MockClientHttpRequest(HttpMethod.GET, uri).apply {
            headers.add("X-Header-1", "value1")
            headers.add("X-Header-2", "value2")
            headers.add("X-Header-1", "value9")
        }

    }

}
