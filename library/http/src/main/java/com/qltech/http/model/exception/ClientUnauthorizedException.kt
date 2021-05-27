package com.qltech.http.model.exception

import com.qltech.http.configure.ResponseCode
import com.qltech.http.model.BaseResponse

class ClientUnauthorizedException(response: BaseResponse<*>) :
    ServerException(ResponseCode.ERROR_CLIENT_UNAUTHORIZED, response)

