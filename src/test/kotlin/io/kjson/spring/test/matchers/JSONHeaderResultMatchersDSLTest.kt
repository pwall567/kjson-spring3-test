/*
 * @(#) JSONHeaderResultMatchersDSLTest.kt
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

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.expect

import java.util.UUID

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

import io.kjson.spring.JSONSpring
import io.kjson.spring.test.TestConfiguration
import io.kjson.spring.test.data.RequestData
import io.kjson.spring.test.postForJSON

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestConfiguration::class])
@ComponentScan(basePackageClasses = [JSONSpring::class])
@AutoConfigureMockMvc
class JSONHeaderResultMatchersDSLTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `should test returned header`() {
        mockMvc.postForJSON("/returnheader") {
            content = RequestData(uuid, headerName)
        }.andExpect {
            status { isOk() }
            header {
                string(headerName, uuid.toString())
            }
        }
    }

    @Test fun `should throw error on incorrect returned header`() {
        assertFailsWith<AssertionError> {
            mockMvc.postForJSON("/returnheader") {
                content = RequestData(uuid, headerName)
            }.andExpect {
                status { isOk() }
                header {
                    string(headerName, "WRONG")
                }
            }
        }.let {
            expect("Response header '$headerName' expected:<WRONG> but was:<$uuid>") { it.message }
        }
    }

    @Test fun `should test returned header exists`() {
        mockMvc.postForJSON("/returnheader") {
            content = RequestData(uuid, headerName)
        }.andExpect {
            status { isOk() }
            header {
                exists(headerName)
            }
        }
    }

    @Test fun `should throw error when returned header does not exist`() {
        assertFailsWith<AssertionError> {
            mockMvc.postForJSON("/returnheader") {
                content = RequestData(uuid, headerName)
            }.andExpect {
                status { isOk() }
                header {
                    exists("UNKNOWN")
                }
            }
        }.let {
            expect("Response should contain header 'UNKNOWN'") { it.message }
        }
    }

    @Test fun `should test returned header does not exist`() {
        mockMvc.postForJSON("/returnheader") {
            content = RequestData(uuid, headerName)
        }.andExpect {
            status { isOk() }
            header {
                doesNotExist("NOT-PRESENT")
            }
        }
    }

    @Test fun `should throw error when unexpected header does exist`() {
        assertFailsWith<AssertionError> {
            mockMvc.postForJSON("/returnheader") {
                content = RequestData(uuid, headerName)
            }.andExpect {
                status { isOk() }
                header {
                    doesNotExist(headerName)
                }
            }
        }.let {
            expect("Response should not contain header 'x-test-header'") { it.message }
        }
    }

    companion object {
        const val headerName = "x-test-header"
        val uuid: UUID = UUID.fromString("4db82b50-e7b6-11ed-97eb-9b2c3c58aad6")
    }

}
