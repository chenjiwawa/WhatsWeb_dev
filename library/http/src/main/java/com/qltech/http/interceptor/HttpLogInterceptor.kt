package com.qltech.http.interceptor

import com.qltech.common.utils.XLog
import com.qltech.http.extensions.getBody
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import java.util.concurrent.TimeUnit

class HttpLogInterceptor : Interceptor {

    companion object {
        private const val TAG = "HttpLogInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val tag = request.url().encodedPath()

        val connection = chain.connection()
        //获取请求的协议
        val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
        XLog.i(TAG, "[intercept] req_tag_url $tag: ${request.method()} ${request.url()} $protocol")
        XLog.i(TAG, "[intercept] req_headers $tag: \n${request.headers()}")
        XLog.i(TAG, "[intercept] req_body $tag: ${request.getBody()}")

        val timeComputer = TimeComputer()

        var statusCode = -1

        return try {
            chain.proceed(request).also { response ->
                statusCode = response.code()
                val responseBody = response.getBody()
                XLog.i(TAG, "[intercept] resp_headers $tag: \n${response.headers()}")
                val responseMessage =
                    "[intercept] req_tag_response($statusCode) $tag(${timeComputer.getTookMs()}ms): $responseBody"
                if (statusCode in 200 until 300) {
                    XLog.i(TAG, responseMessage)
                } else {
                    XLog.e(TAG, responseMessage)
                }
            }
        } catch (e: Exception) {
            XLog.e(
                TAG,
                "[intercept] req_tag_error($statusCode) $tag(${timeComputer.getTookMs()}ms): ${e.message}"
            )
            throw e
        }
    }
}

private class TimeComputer {
    private val startNs = System.nanoTime()

    fun getTookMs(): Long {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
    }
}