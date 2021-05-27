package com.qltech.http.extensions

import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets

fun Request.getBody(): String {
    return body()?.let { requestBody ->
        val buffer = Buffer()
        try {
            requestBody.writeTo(buffer)
            val charset = requestBody.contentType()?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8
            buffer.readString(charset)
        } catch (e: IOException) {
            e.toString()
        }
    } ?: ""
}

fun Response.getBody(): String {
    return body()?.let { responseBody ->
        try {
            val source = responseBody.source().apply {
                request(Long.MAX_VALUE) // 缓存整个响应体
            }
            val buffer = source.buffer
            val charset = responseBody.contentType()?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8
            buffer.clone().readString(charset)
        } catch (e: IOException) {
            e.toString()
        }
    } ?: ""
}