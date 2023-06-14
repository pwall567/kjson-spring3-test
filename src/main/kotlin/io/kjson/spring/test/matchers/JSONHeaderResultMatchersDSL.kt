/*
 * @(#) JSONHeaderResultMatchersDSL.kt
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
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.assertFalse
import org.springframework.test.util.AssertionErrors.assertTrue

import io.kjson.spring.test.JSONResultActions

/**
 * DSL class to provide `header` matching functions for `MockMvc`.
 *
 * @author  Peter Wall
 */
class JSONHeaderResultMatchersDSL(private val resultActions: JSONResultActions) {

    fun string(name: String, value: String) {
        assertEquals("Response header '$name'", value, resultActions.mvcResult.response.getHeader(name))
    }

    fun string(name: String, matcher: Matcher<String>) {
        assertThat("Response header '$name'", resultActions.mvcResult.response.getHeader(name), matcher)
    }

    fun exists(name: String) {
        assertTrue("Response should contain header '$name'", resultActions.mvcResult.response.containsHeader(name))
    }

    fun doesNotExist(name: String) {
        assertFalse("Response should not contain header '$name'", resultActions.mvcResult.response.containsHeader(name))
    }

}
