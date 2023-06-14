/*
 * @(#) MediaTypeMatcher.kt
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

/**
 * A `Matcher` that matches a [MediaType], comparing only the type and subtype fields.
 *
 * @author  Peter Wall
 */
class MediaTypeMatcher(private val mediaType: MediaType) : BaseMatcher<String>() {

    override fun describeTo(description: Description) {
        description.appendText("valid Media Type")
    }

    override fun matches(actual: Any?): Boolean {
        if (actual !is String)
            return false
        val actualMediaType: MediaType = try {
            MediaType.parseMediaType(actual)
        } catch (_: Exception) {
            return false
        }
        return actualMediaType.equalsTypeAndSubtype(mediaType)
    }

}
