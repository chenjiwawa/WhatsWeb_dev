package com.qltech.http.configure

object ResponseCode {
    const val SUCCESS = 200

    const val RESPONSE_CODE_SUCCESS = 10000

    const val ERROR_SERVER_INTERNAL = 400
    const val ERROR_CLIENT_UNAUTHORIZED = 401
    const val ERROR_ACCESS_DENIED = 403
    const val ERROR_UNKNOWN = 600
}