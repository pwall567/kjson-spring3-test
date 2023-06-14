/*
 * @(#) JSONMockHttpServletRequestDSL.kt
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

import java.security.Principal
import java.util.Locale
import jakarta.servlet.http.Cookie

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.util.MultiValueMap

import io.kjson.JSONConfig
import io.kjson.stringifyJSON

/**
 * This is a replacement for the `MockHttpServletRequestDsl` returned by the Spring Kotlin extension functions.  It is
 * used by `JSONMockMvc` and it operates mostly as a like-for-like replacement of the original class, but it adds
 * functionality to convert request content using the [JSONConfig] instance discovered through Spring auto-wiring.
 *
 * @author  Peter Wall
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class JSONMockHttpServletRequestDSL(
    private val builder: MockHttpServletRequestBuilder,
    private val config: JSONConfig,
) {

    var contextPath: String? = null
    var servletPath: String? = null
    var pathInfo: String? = null
    var secure: Boolean? = null
    var characterEncoding: String? = null
    var content: Any? = null
    var accept: MediaType? = null
    var contentType: MediaType? = null
    var params: MultiValueMap<String, String>? = null
    var sessionAttrs: Map<String, Any>? = null
    var flashAttrs: Map<String, Any>? = null
    var session: MockHttpSession? = null
    var principal: Principal? = null

    fun accept(vararg mediaTypes: MediaType) {
        builder.accept(*mediaTypes)
    }

    fun headers(headers: HttpHeaders.() -> Unit) {
        builder.headers(HttpHeaders().apply(headers))
    }

    fun header(name: String, vararg values: Any) {
        builder.header(name, *values)
    }

    fun param(name: String, vararg values: String) {
        builder.param(name, *values)
    }

    fun cookie(vararg cookies: Cookie) {
        builder.cookie(*cookies)
    }

    fun locale(vararg locales: Locale) {
        builder.locale(*locales)
    }

    fun requestAttr(name: String, value: Any) {
        builder.requestAttr(name, value)
    }

    fun sessionAttr(name: String, value: Any) {
        builder.sessionAttr(name, value)
    }

    fun flashAttr(name: String, value: Any) {
        builder.flashAttr(name, value)
    }

    fun with(processor: RequestPostProcessor) {
        builder.with(processor)
    }

    fun merge(parent: MockHttpServletRequestBuilder?) {
        builder.merge(parent)
    }

    fun perform(mockMvc: MockMvc): JSONResultActionsDSL {
        contextPath?.let { builder.contextPath(it) }
        servletPath?.let { builder.servletPath(it) }
        pathInfo?.let { builder.pathInfo(it) }
        secure?.let { builder.secure(it) }
        characterEncoding?.let { builder.characterEncoding(it) }
        content?.let { builder.content(when (it) {
            is ByteArray -> it
            is String -> it.toByteArray()
            else -> it.stringifyJSON(config).toByteArray().also {
                if (contentType == null)
                    contentType = MediaType.APPLICATION_JSON
            }
        }) }
        accept?.let { builder.accept(it) }
        contentType?.let { builder.contentType(it) }
        params?.let { builder.params(it) }
        sessionAttrs?.let { builder.sessionAttrs(it) }
        flashAttrs?.let { builder.flashAttrs(it) }
        session?.let { builder.session(it) }
        principal?.let { builder.principal(it) }
        return JSONResultActionsDSL(mockMvc.perform(builder))
    }

    fun contentJSON(block: () -> Any?) {
        contentJSON(block())
    }

    fun contentJSON(data: Any?) {
        contentType = MediaType.APPLICATION_JSON
        content = data.stringifyJSON(config)
    }

}
