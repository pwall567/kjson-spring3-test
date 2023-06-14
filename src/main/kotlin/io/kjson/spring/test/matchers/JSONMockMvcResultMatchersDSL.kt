/*
 * @(#) JSONMockMvcResultMatchersDSL.kt
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

import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import io.kjson.spring.test.JSONResultActions
import io.kjson.test.JSONExpect

/**
 * DSL class to provide result matching functions for `MockMvc`.
 *
 * @author  Peter Wall
 */
@Suppress("MemberVisibilityCanBePrivate")
class JSONMockMvcResultMatchersDSL(val resultActions: JSONResultActions) {

    val response: MockHttpServletResponse
        get() = resultActions.mvcResult.response

    val contentAsString: String
        get() = resultActions.mvcResult.response.contentAsString

    fun status(dsl: JSONStatusResultMatchersDSL.() -> Unit) {
        JSONStatusResultMatchersDSL(resultActions.mvcResult.response).dsl()
    }

    fun content(dsl: JSONContentResultMatchersDSL.() -> Unit) {
        JSONContentResultMatchersDSL(resultActions).dsl()
    }

    fun contentMatchesJSON(tests: JSONExpect.() -> Unit) {
        MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON).
                match(resultActions.mvcResult)
        JSONExpect.expectJSON(resultActions.mvcResult.response.contentAsString, tests)
    }

    fun header(dsl: JSONHeaderResultMatchersDSL.() -> Unit) {
        JSONHeaderResultMatchersDSL(resultActions).dsl()
    }

}
