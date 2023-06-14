/*
 * @(#) TestController.kt
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

import io.kjson.spring.test.data.OtherData
import java.time.LocalDate
import java.util.UUID

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

import io.kjson.spring.test.data.RequestData
import io.kjson.spring.test.data.ResponseData

@RestController
@Suppress("unused")
class TestController {

    @GetMapping("/testendpoint", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDummyData(): ResponseData {
        return ResponseData(
            date = LocalDate.of(2022, 7, 6),
            extra = "Hello!",
        )
    }

    @GetMapping("/testendpoint2", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDummyData2(): ResponseData {
        return ResponseData(
            date = LocalDate.of(2022, 7, 14),
            extra = "Goodbye!",
        )
    }

    @GetMapping("/testendpoint3/{extra}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDummyData3(
        @PathVariable extra: String,
    ): ResponseData {
        return ResponseData(
            date = LocalDate.of(2023, 5, 1),
            extra = extra,
        )
    }

    @GetMapping("/testendpoint4/{extra}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getOtherDara(
        @PathVariable extra: String,
    ): OtherData {
        return OtherData(
            a = extra,
            b = extra,
        )
    }

    @PostMapping("/testendpoint")
    fun dummyPost(
        @RequestBody requestData: RequestData,
    ): ResponseData {
        return ResponseData(
            date = LocalDate.of(2022, 7, 6),
            extra = "${requestData.id}|${requestData.name}"
        )

    }

    @GetMapping("/testheaders")
    fun getHeaders(
        @RequestHeader headers: MultiValueMap<String, String>
    ): MultiValueMap<String, String> {
        return headers
    }

    @PostMapping("/returnheader")
    fun returnHeader(
        @RequestBody requestData: RequestData,
    ): ResponseEntity<Map<String, UUID>> {
        val headers = HttpHeaders().apply { add(requestData.name, requestData.id.toString()) }
        return ResponseEntity(mapOf(requestData.name to requestData.id), headers, HttpStatus.OK)
    }

}
