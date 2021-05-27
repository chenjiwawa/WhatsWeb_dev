package com.qltech.http.model.exception

import com.qltech.http.configure.ResponseCode
import com.qltech.http.model.BaseResponse

class AccessDeniedException(response: BaseResponse<*>) :
    ServerException(ResponseCode.ERROR_ACCESS_DENIED, response)