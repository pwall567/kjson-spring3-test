/*
 * @(#) JSONStatusResultMatchersDSL.kt
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

import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.util.AssertionErrors.fail

/**
 * DSL class to provide `status` matching functions for `MockMvc`.
 *
 * @author  Peter Wall
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "deprecation", "RedundantSuppression")
class JSONStatusResultMatchersDSL(private val response: MockHttpServletResponse) {

    fun is1xxInformational() {
        response.status.let {
            if (it !in 100..199)
                fail("Status", "1xx INFORMATIONAL", findStatus(it))
        }
    }

    fun is2xxSuccessful() {
        response.status.let {
            if (it !in 200..299)
                fail("Status", "2xx SUCCESSFUL", findStatus(it))
        }
    }

    fun is3xxRedirection() {
        response.status.let {
            if (it !in 300..399)
                fail("Status", "3xx REDIRECTION", findStatus(it))
        }
    }

    fun is4xxClientError() {
        response.status.let {
            if (it !in 400..499)
                fail("Status", "4xx CLIENT ERROR", findStatus(it))
        }
    }

    fun is5xxServerError() {
        response.status.let {
            if (it !in 500..599)
                fail("Status", "5xx SERVER ERROR", findStatus(it))
        }
    }

    fun reason(reason: String) {
        response.errorMessage.let {
            if (it != reason)
                fail("Response status reason", reason, it)
        }
    }

    fun isContinue() {
        isEqualTo(HttpStatus.CONTINUE)
    }

    fun isSwitchingProtocols() {
        isEqualTo(HttpStatus.SWITCHING_PROTOCOLS)
    }

    fun isProcessing() {
        isEqualTo(HttpStatus.PROCESSING)
    }

    fun isCheckpoint() {
        isEqualTo(HttpStatus.CHECKPOINT)
    }

    fun isOk() {
        isEqualTo(HttpStatus.OK)
    }

    fun isCreated() {
        isEqualTo(HttpStatus.CREATED)
    }

    fun isAccepted() {
        isEqualTo(HttpStatus.ACCEPTED)
    }

    fun isNonAuthoritativeInformation() {
        isEqualTo(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    }

    fun isNoContent() {
        isEqualTo(HttpStatus.NO_CONTENT)
    }

    fun isResetContent() {
        isEqualTo(HttpStatus.RESET_CONTENT)
    }

    fun isPartialContent() {
        isEqualTo(HttpStatus.PARTIAL_CONTENT)
    }

    fun isMultiStatus() {
        isEqualTo(HttpStatus.MULTI_STATUS)
    }

    fun isAlreadyReported() {
        isEqualTo(HttpStatus.ALREADY_REPORTED)
    }

    fun isImUsed() {
        isEqualTo(HttpStatus.IM_USED)
    }

    fun isMultipleChoices() {
        isEqualTo(HttpStatus.MULTIPLE_CHOICES)
    }

    fun isMovedPermanently() {
        isEqualTo(HttpStatus.MOVED_PERMANENTLY)
    }

    fun isFound() {
        isEqualTo(HttpStatus.FOUND)
    }

    fun isMovedTemporarily() {
        isEqualTo(HttpStatus.MOVED_TEMPORARILY)
    }

    fun isSeeOther() {
        isEqualTo(HttpStatus.SEE_OTHER)
    }

    fun isNotModified() {
        isEqualTo(HttpStatus.NOT_MODIFIED)
    }

    fun isUseProxy() {
        isEqualTo(HttpStatus.USE_PROXY)
    }

    fun isTemporaryRedirect() {
        isEqualTo(HttpStatus.TEMPORARY_REDIRECT)
    }

    fun isPermanentRedirect() {
        isEqualTo(HttpStatus.PERMANENT_REDIRECT)
    }

    fun isBadRequest() {
        isEqualTo(HttpStatus.BAD_REQUEST)
    }

    fun isUnauthorized() {
        isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    fun isPaymentRequired() {
        isEqualTo(HttpStatus.PAYMENT_REQUIRED)
    }

    fun isForbidden() {
        isEqualTo(HttpStatus.FORBIDDEN)
    }

    fun isNotFound() {
        isEqualTo(HttpStatus.NOT_FOUND)
    }

    fun isMethodNotAllowed() {
        isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
    }

    fun isNotAcceptable() {
        isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    fun isProxyAuthenticationRequired() {
        isEqualTo(HttpStatus.PROXY_AUTHENTICATION_REQUIRED)
    }

    fun isRequestTimeout() {
        isEqualTo(HttpStatus.REQUEST_TIMEOUT)
    }

    fun isConflict() {
        isEqualTo(HttpStatus.CONFLICT)
    }

    fun isGone() {
        isEqualTo(HttpStatus.GONE)
    }

    fun isLengthRequired() {
        isEqualTo(HttpStatus.LENGTH_REQUIRED)
    }

    fun isPreconditionFailed() {
        isEqualTo(HttpStatus.PRECONDITION_FAILED)
    }

    fun isPayloadTooLarge() {
        isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE)
    }

    fun isRequestEntityTooLarge() {
        isEqualTo(HttpStatus.REQUEST_ENTITY_TOO_LARGE)
    }

    fun isUriTooLong() {
        isEqualTo(HttpStatus.URI_TOO_LONG)
    }

    fun isRequestUriTooLong() {
        isEqualTo(HttpStatus.REQUEST_URI_TOO_LONG)
    }

    fun isUnsupportedMediaType() {
        isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    fun isRequestedRangeNotSatisfiable() {
        isEqualTo(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
    }

    fun isExpectationFailed() {
        isEqualTo(HttpStatus.EXPECTATION_FAILED)
    }

    fun isIAmATeapot() {
        isEqualTo(HttpStatus.I_AM_A_TEAPOT)
    }

    fun isInsufficientSpaceOnResource() {
        isEqualTo(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE)
    }

    fun isMethodFailure() {
        isEqualTo(HttpStatus.METHOD_FAILURE)
    }

    fun isDestinationLocked() {
        isEqualTo(HttpStatus.DESTINATION_LOCKED)
    }

    fun isUnprocessableEntity() {
        isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    fun isLocked() {
        isEqualTo(HttpStatus.LOCKED)
    }

    fun isFailedDependency() {
        isEqualTo(HttpStatus.FAILED_DEPENDENCY)
    }

    fun isTooEarly() {
        isEqualTo(HttpStatus.TOO_EARLY)
    }

    fun isUpgradeRequired() {
        isEqualTo(HttpStatus.UPGRADE_REQUIRED)
    }

    fun isPreconditionRequired() {
        isEqualTo(HttpStatus.PRECONDITION_REQUIRED)
    }

    fun isTooManyRequests() {
        isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }

    fun isRequestHeaderFieldsTooLarge() {
        isEqualTo(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE)
    }

    fun isUnavailableForLegalReasons() {
        isEqualTo(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
    }

    fun isInternalServerError() {
        isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun isNotImplemented() {
        isEqualTo(HttpStatus.NOT_IMPLEMENTED)
    }

    fun isBadGateway() {
        isEqualTo(HttpStatus.BAD_GATEWAY)
    }

    fun isServiceUnavailable() {
        isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }

    fun isGatewayTimeout() {
        isEqualTo(HttpStatus.GATEWAY_TIMEOUT)
    }

    fun isHttpVersionNotSupported() {
        isEqualTo(HttpStatus.HTTP_VERSION_NOT_SUPPORTED)
    }

    fun isVariantAlsoNegotiates() {
        isEqualTo(HttpStatus.VARIANT_ALSO_NEGOTIATES)
    }

    fun isInsufficientStorage() {
        isEqualTo(HttpStatus.INSUFFICIENT_STORAGE)
    }

    fun isLoopDetected() {
        isEqualTo(HttpStatus.LOOP_DETECTED)
    }

    fun isBandwidthLimitExceeded() {
        isEqualTo(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
    }

    fun isNotExtended() {
        isEqualTo(HttpStatus.NOT_EXTENDED)
    }

    fun isNetworkAuthenticationRequired() {
        isEqualTo(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)
    }

    fun isEqualTo(expected: Int) {
        response.status.let {
            if (it != expected)
                fail("Status", findStatus(expected), findStatus(it))
        }
    }

    fun isEqualTo(expected: HttpStatus) {
        response.status.let {
            if (it != expected.value())
                fail("Status", expected.toString(), findStatus(it))
        }
    }

    private fun findStatus(code: Int): String =
            HttpStatus.entries.find { it.value() == code }?.toString() ?: "$code UNKNOWN"

}
