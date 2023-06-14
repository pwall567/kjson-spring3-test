/*
 * @(#) JSONMatcher.kt
 *
 * kjson-spring3-test  Spring Boot 3 JSON testing functions for kjson
 * Copyright (c) 2022 Peter Wall
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

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.ContentResultMatchersDsl
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl

import io.kjson.test.JSONExpect

/**
 * A `Matcher` class that allows Spring `mockMvc` JSON responses to be tested using the capabilities of the
 * [kjson-test](https://github.com/pwall567/kjson-test) library.
 *
 * @author  Peter Wall
 */
class JSONMatcher private constructor(private val tests: JSONExpect.() -> Unit) : BaseMatcher<String>() {

    override fun matches(actual: Any?): Boolean {
        if (actual !is String)
            return false
        JSONExpect.expectJSON(actual, tests)
        return true
    }

    override fun describeTo(description: Description) {
        description.appendText("valid JSON")
    }

    companion object {

        fun ContentResultMatchersDsl.matchesJSON(tests: JSONExpect.() -> Unit) = string(JSONMatcher(tests))

        fun MockMvcResultMatchersDsl.contentMatchesJSON(tests: JSONExpect.() -> Unit) {
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                matchesJSON(tests)
            }
        }

    }

}
