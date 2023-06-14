/*
 * @(#) JSONContentResultMatchersDSLTest.kt
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

package io.kjson.spring.test.matchers

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.expect

import java.time.LocalDate

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

import io.kjson.spring.JSONSpring
import io.kjson.spring.test.TestConfiguration
import io.kjson.spring.test.getForJSON
import io.kjson.spring.test.matchers.JSONMatcher.Companion.matchesJSON

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestConfiguration::class])
@ComponentScan(basePackageClasses = [JSONSpring::class])
@AutoConfigureMockMvc
class JSONContentResultMatchersDSLTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `should allow access to response object`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            expect("""{"date":"2022-07-06","extra":"Hello!"}""") { response.contentAsString }
        }
    }

    @Test fun `should allow access to response content string`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            expect("""{"date":"2022-07-06","extra":"Hello!"}""") { contentAsString }
        }
    }

    @Test fun `should test content type`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            content {
                contentType("application/json;charset=UTF-8")
            }
        }
    }

    @Test fun `should throw error on incorrect content type`() {
        assertFailsWith<AssertionError> {
            mockMvc.getForJSON("/testendpoint").andExpect {
                status { isOk() }
                content {
                    contentType("text/plain")
                }
            }
        }.let {
            expect("Content type expected:<text/plain> but was:<application/json;charset=UTF-8>") { it.message }
        }
    }

    @Test fun `should test content type using MediaType`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            content {
                contentType(MediaType("application", "json", mapOf("charset" to "UTF-8")))
            }
        }
    }

    @Test fun `should test content type compatible with`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith("application/json")
            }
        }
    }

    @Test fun `should throw error on incorrect content type compatible with`() {
        assertFailsWith<AssertionError> {
            mockMvc.getForJSON("/testendpoint").andExpect {
                status { isOk() }
                content {
                    contentTypeCompatibleWith("image/jpeg")
                }
            }
        }.let {
            expect("Content type [application/json;charset=UTF-8] is not compatible with [image/jpeg]") { it.message }
        }
    }

    @Test fun `should test content type compatible with using MediaType`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            }
        }
    }

    @Test fun `should test encoding`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            content {
                encoding("UTF-8")
            }
        }
    }

    @Test fun `should test content as string`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            content {
                string(right)
            }
        }
    }

    @Test fun `should throw error on incorrect content as string`() {
        assertFailsWith<AssertionError> {
            mockMvc.getForJSON("/testendpoint").andExpect {
                status { isOk() }
                content {
                    string(wrong)
                }
            }
        }.let {
            expect("""Response content expected:<$wrong> but was:<$right>""") {
                it.message
            }
        }
    }

    @Test fun `should test content as byte array`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            content {
                bytes(right.toByteArray())
            }
        }
    }

    @Test fun `should test content using matchesJSON`() {
        mockMvc.get("/testendpoint").andExpect {
            status { isOk() }
            content {
                matchesJSON {
                    property("date", LocalDate.of(2022, 7, 6))
                    property("extra", "Hello!")
                }
            }
        }
    }

    @Test fun `should throw error on incorrect content using matchesJSON`() {
        assertFailsWith<AssertionError> {
            mockMvc.get("/testendpoint").andExpect {
                status { isOk() }
                content {
                    matchesJSON {
                        property("date", LocalDate.of(2022, 7, 6))
                        property("extra", "Goodbye!")
                    }
                }
            }
        }.let {
            expect("""/extra: JSON value doesn't match - expected "Goodbye!", was "Hello!"""") { it.message }
        }
    }

    companion object {
        const val right = """{"date":"2022-07-06","extra":"Hello!"}"""
        const val wrong = """{"date":"2022-07-06","extra":"Goodbye!"}"""
    }

}
