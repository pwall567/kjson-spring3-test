/*
 * @(#) JSONContentResultMatchersDSL.kt
 *
 * kjson-spring3-test  Spring Boot 3 JSON testing functions for kjson
 * Copyright (c) 2023 Peter Wall
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

package io.kjson.spring.test.matchers

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.springframework.http.MediaType
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.fail
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import io.kjson.spring.test.JSONResultActions
import io.kjson.test.JSONExpect

/**
 * DSL class to provide `content` matching functions for `MockMvc`.
 *
 * @author  Peter Wall
 */
@Suppress("MemberVisibilityCanBePrivate")
class JSONContentResultMatchersDSL(val resultActions: JSONResultActions) {

    private val matchers = MockMvcResultMatchers.content()

    fun contentType(contentType: String) {
        matchers.contentType(contentType).match(resultActions.mvcResult)
    }

    fun contentType(contentType: MediaType) {
        matchers.contentType(contentType).match(resultActions.mvcResult)
    }

    fun contentTypeCompatibleWith(contentType: String) {
        matchers.contentTypeCompatibleWith(contentType).match(resultActions.mvcResult)
    }

    fun contentTypeCompatibleWith(contentType: MediaType) {
        matchers.contentTypeCompatibleWith(contentType).match(resultActions.mvcResult)
    }

    fun encoding(encoding: String) {
        matchers.encoding(encoding).match(resultActions.mvcResult)
    }

    fun string(string: String) {
        assertEquals("Response content", string, resultActions.mvcResult.response.contentAsString)
    }

    fun string(test: (String) -> Boolean) {
        if (!test(resultActions.mvcResult.response.contentAsString))
            fail("Response content doesn't match test")
    }

    @Deprecated("The use of Matcher may be removed in a future version", ReplaceWith("string { test(it) }"))
    fun string(matcher: Matcher<String>) {
        assertThat("Response content", resultActions.mvcResult.response.contentAsString, matcher)
    }

    fun bytes(bytes: ByteArray) {
        assertEquals("Response content", bytes, resultActions.mvcResult.response.contentAsByteArray)
    }

    fun matchesJSON(tests: JSONExpect.() -> Unit) {
        JSONExpect.expectJSON(resultActions.mvcResult.response.contentAsString, tests)
    }

}
