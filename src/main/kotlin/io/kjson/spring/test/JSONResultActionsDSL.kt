/*
 * @(#) JSONResultActionsDSL.kt
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

package io.kjson.spring.test

import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions

import io.kjson.spring.test.matchers.JSONMockMvcResultMatchersDSL

/**
 * Replacement for Spring `ResultActionsDsl` (required because many of the Spring classes have non-public constructors).
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class JSONResultActionsDSL(val resultActions: ResultActions) {

    val jsonResultActions = JSONResultActions(resultActions.andReturn())

    fun andExpect(block: JSONMockMvcResultMatchersDSL.() -> Unit): JSONResultActionsDSL {
        JSONMockMvcResultMatchersDSL(jsonResultActions).block()
        return this
    }

    fun andReturn(): MvcResult = resultActions.andReturn()

}
