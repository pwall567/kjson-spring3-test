/*
 * @(#) JSONMockClientRequest.kt
 *
 * kjson-spring3-test  Spring Boot 3 JSON testing functions for kjson
 * Copyright (c) 2022, 2025 Peter Wall
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

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.mock.http.client.MockClientHttpRequest

import io.kjson.JSONConfig
import io.kjson.JSONValue
import io.kjson.parser.Parser

/**
 * Wrapper class for [MockClientHttpRequest] which simplifies access to headers, query parameters and [JSONConfig].
 *
 * @author  Peter Wall
 */
class JSONMockClientRequest(
    private val mockClientHttpRequest: MockClientHttpRequest,
    val config: JSONConfig,
) : HttpRequest {

    val uri: URI
        get() = mockClientHttpRequest.uri

    val bodyAsString: String
        get() = mockClientHttpRequest.bodyAsString

    val bodyAsBytes: ByteArray
        get() = mockClientHttpRequest.bodyAsBytes

    val bodyAsJSON: JSONValue? by lazy {
        Parser.parse(mockClientHttpRequest.bodyAsString, config.parseOptions)
    }

    val paramsMap: Map<String, List<String?>> by lazy {
        decodeQueryParams(mockClientHttpRequest.uri)
    }

    override fun getMethod(): HttpMethod = mockClientHttpRequest.method

    override fun getURI(): URI = mockClientHttpRequest.uri

    override fun getHeaders(): HttpHeaders = mockClientHttpRequest.headers

    override fun getAttributes(): MutableMap<String, Any> = mockClientHttpRequest.attributes

    fun getHeaders(name: String): List<String>? = headers[name]

    fun getHeader(name: String): String? {
        val header = headers[name] ?: return null
        if (header.size != 1)
            throw AssertionError("Request [$name] header - multiple headers (${header.size})")
        return header[0]
    }

    fun hasHeader(name: String): Boolean = mockClientHttpRequest.headers.containsKey(name)

    fun getParams(name: String): List<String?>? = paramsMap[name]

    fun getParam(name: String): String? {
        val param = paramsMap[name] ?: return null
        if (param.size != 1)
            throw AssertionError("Request [$name] param - multiple params (${param.size})")
        return param[0]
    }

    fun hasParam(name: String): Boolean = paramsMap.containsKey(name)

    companion object {

        fun decodeQueryParams(uri: URI): Map<String, List<String?>> =
            LinkedHashMap<String, List<String?>>().apply {
                uri.query?.split('&')?.forEach {
                    val eqIndex = it.indexOf('=')
                    if (eqIndex < 0)
                        addOrInsert(it.trim(), null)
                    else {
                        val key = it.substring(0, eqIndex).trim()
                        val value = it.substring(eqIndex + 1).trim()
                        addOrInsert(key, value)
                    }
                }
            }

        private fun LinkedHashMap<String, List<String?>>.addOrInsert(key: String, value: String?) {
            put(key, get(key)?.let { it + value } ?: listOf(value))
        }

    }

}
