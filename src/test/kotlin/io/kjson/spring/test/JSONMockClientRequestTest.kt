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

import java.net.URI

import org.springframework.http.HttpMethod
import org.springframework.mock.http.client.MockClientHttpRequest

import io.kstuff.test.shouldBe
import io.kstuff.test.shouldBeNonNull
import io.kstuff.test.shouldBeType
import io.kstuff.test.shouldThrow

import io.kjson.JSON.asInt
import io.kjson.JSONConfig
import io.kjson.JSONObject

class JSONMockClientRequestTest {

    @Test fun `should return method`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        jmcr.method shouldBe HttpMethod.GET
    }

    @Test fun `should return URI`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        jmcr.uri shouldBe uri
    }

    @Test fun `should return body as string`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        mockClientHttpRequest.body.writer().use {
            it.append("DATA!")
        }
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        jmcr.bodyAsString shouldBe "DATA!"
    }

    @Test fun `should return body as byte array`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        mockClientHttpRequest.body.writer().use {
            it.append("DATA!")
        }
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        jmcr.bodyAsBytes shouldBe "DATA!".toByteArray()
    }

    @Test fun `should return body as JSON`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        mockClientHttpRequest.body.writer().use {
            it.append("""{"aaa":987}""")
        }
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        val json = jmcr.bodyAsJSON
        json.shouldBeType<JSONObject>()
        json["aaa"].asInt shouldBe 987
    }

    @Test fun `should return map of query parameters`() {
        val mockClientHttpRequest = MockClientHttpRequest(HttpMethod.GET, uri)
        val jmcr = JSONMockClientRequest(mockClientHttpRequest, config)
        val params = jmcr.paramsMap
        params.size shouldBe 2
        with(params["abc"]) {
            shouldBeNonNull()
            size shouldBe 2
            this[0] shouldBe "123"
            this[1] shouldBe "789"
        }
        with(params["xyz"]) {
            shouldBeNonNull()
            size shouldBe 1
            this[0] shouldBe "456"
        }
        params["mmm"] shouldBe null
    }

    @Test fun `should return all headers`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        val headers = jmcr.headers
        headers.size shouldBe 2
        with(headers["X-Header-1"]) {
            shouldBeNonNull()
            size shouldBe 2
            this[0] shouldBe "value1"
            this[1] shouldBe "value9"
        }
        with(headers["X-Header-2"]) {
            shouldBeNonNull()
            size shouldBe 1
            this[0] shouldBe "value2"
        }
        headers["X-Header-9"] shouldBe null
    }

    @Test fun `should return specific headers`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        val headers1 = jmcr.getHeaders("X-Header-1")
        headers1.shouldBeNonNull()
        headers1.size shouldBe 2
        headers1[0] shouldBe "value1"
        headers1[1] shouldBe "value9"
        val headers2 = jmcr.getHeaders("X-Header-2")
        headers2.shouldBeNonNull()
        headers2.size shouldBe 1
        headers2[0] shouldBe "value2"
        jmcr.getHeaders("X-Header-3") shouldBe null
    }

    @Test fun `should return individual header`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        jmcr.getHeader("X-Header-2") shouldBe "value2"
        jmcr.getHeader("X-Header-3") shouldBe null
    }

    @Test fun `should throw exception on get individual header when multiple present`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        shouldThrow<AssertionError>("Request [X-Header-1] header - multiple headers (2)") {
            jmcr.getHeader("X-Header-1")
        }
    }

    @Test fun `should report headers present`() {
        val jmcr = JSONMockClientRequest(requestWithHeaders, config)
        jmcr.hasHeader("X-Header-1") shouldBe true
        jmcr.hasHeader("X-Header-2") shouldBe true
        jmcr.hasHeader("X-Header-3") shouldBe false
    }

    @Test fun `should return specific query parameters`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        val abc = jmcr.getParams("abc")
        abc.shouldBeNonNull()
        abc.size shouldBe 2
        abc[0] shouldBe "123"
        abc[1] shouldBe "789"
        val xyz = jmcr.getParams("xyz")
        xyz.shouldBeNonNull()
        xyz.size shouldBe 1
        xyz[0] shouldBe "456"
        jmcr.getParams("pqr") shouldBe null
    }

    @Test fun `should return individual query parameter`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        jmcr.getParam("xyz") shouldBe "456"
        jmcr.getParam("qqq") shouldBe null
    }

    @Test fun `should throw exception on get individual query parameter when multiple present`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        shouldThrow<AssertionError>("Request [abc] param - multiple params (2)") {
            jmcr.getParam("abc")
        }
    }

    @Test fun `should report query parameters present`() {
        val jmcr = JSONMockClientRequest(requestWithParams, config)
        jmcr.hasParam("abc") shouldBe true
        jmcr.hasParam("xyz") shouldBe true
        jmcr.hasParam("aaa") shouldBe false
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
