/*
 * @(#) JSONResponseActions.kt
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

package io.kjson.spring.test

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.RequestMatcher
import org.springframework.test.web.client.ResponseActions
import org.springframework.test.web.client.ResponseCreator
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus

import io.kjson.JSONConfig
import io.kjson.stringifyJSON

/**
 * Replacement implementation for [ResponseActions], allowing access to the [JSONConfig] instance discovered through
 * Spring auto-wiring.
 *
 * @author  Peter Wall
 */
class JSONResponseActions(
    @Suppress("MemberVisibilityCanBePrivate")
    val responseActions: ResponseActions,
    val config: JSONConfig,
) : ResponseActions {

    override fun andExpect(requestMatcher: RequestMatcher): JSONResponseActions {
        responseActions.andExpect(requestMatcher)
        return this
    }

    override fun andRespond(responseCreator: ResponseCreator) {
        responseActions.andRespond(responseCreator)
    }

    fun respondJSON(
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders = HttpHeaders(),
        block: () -> Any?
    ) {
        val body = block().stringifyJSON(config)
        andRespond(withStatus(status).headers(headers).contentType(MediaType.APPLICATION_JSON).body(body))
    }

}
