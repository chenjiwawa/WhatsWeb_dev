package com.qltech.http.model

import com.google.gson.annotations.SerializedName
import com.qltech.http.configure.ResponseCode
import com.qltech.http.model.exception.ServerException

data class BaseResponse<out T>(
    @SerializedName("code")
    val responseCode: Int = 0,
    val message: String = "",
    val data: T? = null
) {
    fun isSuccess(): Boolean = ResponseCode.RESPONSE_CODE_SUCCESS == responseCode
}

fun <T> BaseResponse<T>.transform(): T {
    return if (isSuccess()) {
        data ?: throw IllegalArgumentException("data is null")
    } else {
        throw ServerException(ResponseCode.SUCCESS, this)
    }
}