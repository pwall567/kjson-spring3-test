/*
 * @(#) JSONMockMvcTest.kt
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

import kotlin.test.Test

import java.net.URI
import java.time.LocalDate
import java.util.UUID

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

import io.kjson.spring.test.data.RequestData
import io.kjson.spring.test.matchers.JSONMatcher.Companion.matchesJSON

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestConfiguration::class])
@AutoConfigureMockMvc
class JSONMockMvcTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `should use JSONMockMvc`() {
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

    @Test fun `should perform variable substitution`() {
        mockMvc.getForJSON("/testendpoint3/{extra}", "what").andExpect {
            status { isOk() }
            content {
                matchesJSON {
                    property("date", LocalDate.of(2023, 5, 1))
                    property("extra", "what")
                }
            }
        }
    }

    @Test fun `should use getForJSON`() {
        mockMvc.getForJSON("/testendpoint").andExpect {
            status { isOk() }
            contentMatchesJSON {
                property("date", LocalDate.of(2022, 7, 6))
                property("extra", "Hello!")
            }
        }
    }

    @Test fun `should use getForJSON using URI`() {
        mockMvc.getForJSON(URI("/testendpoint")).andExpect {
            status { isOk() }
            contentMatchesJSON {
                property("date", LocalDate.of(2022, 7, 6))
                property("extra", "Hello!")
            }
        }
    }

    @Test fun `should use getJSON with headers`() {
        mockMvc.getForJSON("/testheaders") {
            header("testheader1", "value1")
        }.andExpect {
            status { isOk() }
            contentMatchesJSON {
                property("testheader1") {
                    count(1)
                    item(0, "value1")
                }
            }
        }
    }

    @Test fun `should use postJSON`() {
        mockMvc.postForJSON("/testendpoint") {
            contentJSON {
                RequestData(id = UUID.fromString("50b4f2c8-fdf8-11ec-be56-3fb4fd705ec6"), name = "Mary")
            }
        }.andExpect {
            status { isOk() }
            contentMatchesJSON {
                property("date", LocalDate.of(2022, 7, 6))
                property("extra", "50b4f2c8-fdf8-11ec-be56-3fb4fd705ec6|Mary")
            }
        }
    }

    @Test fun `should use postJSON using URI`() {
        mockMvc.postForJSON(URI("/testendpoint")) {
            contentJSON {
                RequestData(id = UUID.fromString("50b4f2c8-fdf8-11ec-be56-3fb4fd705ec6"), name = "Mary")
            }
        }.andExpect {
            status { isOk() }
            contentMatchesJSON {
                property("date", LocalDate.of(2022, 7, 6))
                property("extra", "50b4f2c8-fdf8-11ec-be56-3fb4fd705ec6|Mary")
            }
        }
    }

    @Test fun `should use postJSON with automatic JSON conversion`() {
        mockMvc.postForJSON("/testendpoint") {
            content = RequestData(id = UUID.fromString("50b4f2c8-fdf8-11ec-be56-3fb4fd705ec6"), name = "Mary")
        }.andExpect {
            status { isOk() }
            contentMatchesJSON {
                property("date", LocalDate.of(2022, 7, 6))
                property("extra", "50b4f2c8-fdf8-11ec-be56-3fb4fd705ec6|Mary")
            }
        }
    }

    @Test fun `should use discovered JSONConfig`() {
        mockMvc.getForJSON("/testendpoint4/abcde").andExpect {
            status { isOk() }
            content {
                matchesJSON {
                    property("aa", "abcde")
                    property("bb", "edcba")
                }
            }
        }
    }

}
