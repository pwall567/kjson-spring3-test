/*
 * @(#) JSONMockRequestTest.kt
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

import kotlin.test.Test

import java.time.LocalDate
import java.util.UUID

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

import io.kjson.spring.test.data.RequestData

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestConfiguration::class])
@AutoConfigureMockMvc
class JSONMockRequestTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `should use contentJSON`() {
        val id: UUID = UUID.fromString("33e6435c-fd05-11ec-9c79-f751c695d36e")
        val name = "Maggie"
        mockMvc.postForJSON("/testendpoint") {
            contentJSON(RequestData(id, name))
        }.andExpect {
            status { isOk() }
            content {
                matchesJSON {
                    property("date", LocalDate.of(2022, 7, 6))
                    property("extra", "${id}|${name}")
                }
            }
        }
    }

    @Test fun `should use contentJSON lambda`() {
        val id: UUID = UUID.fromString("8d1a1224-fdf9-11ec-8b8c-5b97cd3faaa6")
        val name = "Zebra"
        mockMvc.postForJSON("/testendpoint") {
            contentJSON {
                RequestData(id, name)
            }
        }.andExpect {
            status { isOk() }
            content {
                matchesJSON {
                    property("date", LocalDate.of(2022, 7, 6))
                    property("extra", "${id}|${name}")
                }
            }
        }
    }

}
