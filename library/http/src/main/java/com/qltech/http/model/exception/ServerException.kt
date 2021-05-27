package com.qltech.http.model.exception

import com.qltech.http.model.BaseResponse
import java.net.ConnectException

open class ServerException(val httpCode: Int, val response: BaseResponse<*>) :
    ConnectException(response.getErrorMessage())

private fun BaseResponse<*>.getErrorMessage(): String {
    return if (data is String && data.isNotBlank()) {
        "$message: $data"
    } else {
        message
    }
}