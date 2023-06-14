/*
 * @(#) JSONMockMvc.kt
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

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

/**
 * Make a GET call to a [MockMvc] with the nominated URL (created from a URL template string and an optional set of
 * variables), setting the `Accept` header to `application/json` to indicate that the expected response is JSON.
 * Further details may be added to the request with the optional configuration lambda.
 */
fun MockMvc.getForJSON(
    urlTemplate: String,
    vararg vars: Any?,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.get(urlTemplate, *vars)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a GET call to a [MockMvc] with the nominated URI, setting the `Accept` header to `application/json` to
 * indicate that the expected response is JSON.  Further details may be added to the request with the optional
 * configuration lambda.
 */
fun MockMvc.getForJSON(
    uri: URI,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.get(uri)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a POST call to a [MockMvc] with the nominated URL (created from a URL template string and an optional set of
 * variables). Further details may be added to the request with the optional configuration lambda.
 */
fun MockMvc.postJSON(
    urlTemplate: String,
    vararg vars: Any?,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.post(urlTemplate, *vars)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply(block).perform(this)
}

/**
 * Make a POST call to a [MockMvc] with the nominated URI.  Further details may be added to the request with the
 * optional configuration lambda.
 */
fun MockMvc.postJSON(
    uri: URI,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.post(uri)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply(block).perform(this)
}

/**
 * Make a POST call to a [MockMvc] with the nominated URL (created from a URL template string and an optional set of
 * variables), setting the `Accept` header to `application/json` to indicate that the expected response is JSON.
 * Further details may be added to the request with the optional configuration lambda.
 */
fun MockMvc.postForJSON(
    urlTemplate: String,
    vararg vars: Any?,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.post(urlTemplate, *vars)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a POST call to a [MockMvc] with the nominated URI, setting the `Accept` header to `application/json` to
 * indicate that the expected response is JSON.  Further details may be added to the request with the optional
 * configuration lambda.
 */
fun MockMvc.postForJSON(
    uri: URI,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.post(uri)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a PUT call to a [MockMvc] with the nominated URL (created from a URL template string and an optional set of
 * variables). Further details may be added to the request with the optional configuration lambda.
 */
fun MockMvc.putJSON(
    urlTemplate: String,
    vararg vars: Any?,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.put(urlTemplate, *vars)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply(block).perform(this)
}

/**
 * Make a PUT call to a [MockMvc] with the nominated URI.  Further details may be added to the request with the optional
 * configuration lambda.
 */
fun MockMvc.putJSON(
    uri: URI,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.put(uri)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply(block).perform(this)
}

/**
 * Make a PUT call to a [MockMvc] with the nominated URL (created from a URL template string and an optional set of
 * variables), setting the `Accept` header to `application/json` to indicate that the expected response is JSON.
 * Further details may be added to the request with the optional configuration lambda.
 */
fun MockMvc.putForJSON(
    urlTemplate: String,
    vararg vars: Any?,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.put(urlTemplate, *vars)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a PUT call to a [MockMvc] with the nominated URI, setting the `Accept` header to `application/json` to
 * indicate that the expected response is JSON.  Further details may be added to the request with the optional
 * configuration lambda.
 */
fun MockMvc.putForJSON(
    uri: URI,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.put(uri)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a PATCH call to a [MockMvc] with the nominated URL (created from a URL template string and an optional set of
 * variables), setting the `Accept` header to `application/json` to indicate that the expected response is JSON.
 * Further details may be added to the request with the optional configuration lambda.
 */
fun MockMvc.patchForJSON(
    urlTemplate: String,
    vararg vars: Any?,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.patch(urlTemplate, *vars)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a PATCH call to a [MockMvc] with the nominated URI, setting the `Accept` header to `application/json` to
 * indicate that the expected response is JSON.  Further details may be added to the request with the optional
 * configuration lambda.
 */
fun MockMvc.patchForJSON(
    uri: URI,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.patch(uri)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a DELETE call to a [MockMvc] with the nominated URL (created from a URL template string and an optional set of
 * variables), setting the `Accept` header to `application/json` to indicate that the expected response is JSON.
 * Further details may be added to the request with the optional configuration lambda.
 */
fun MockMvc.deleteForJSON(
    urlTemplate: String,
    vararg vars: Any?,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.delete(urlTemplate, *vars)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}

/**
 * Make a DELETE call to a [MockMvc] with the nominated URI, setting the `Accept` header to `application/json` to
 * indicate that the expected response is JSON.  Further details may be added to the request with the optional
 * configuration lambda.
 */
fun MockMvc.deleteForJSON(
    uri: URI,
    block: JSONMockHttpServletRequestDSL.() -> Unit = {}
): JSONResultActionsDSL {
    val config = JSONConfigCache.getConfig(dispatcherServlet.webApplicationContext)
    val requestBuilder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.delete(uri)
    return JSONMockHttpServletRequestDSL(requestBuilder, config).apply {
        accept(MediaType.APPLICATION_JSON)
    }.apply(block).perform(this)
}
