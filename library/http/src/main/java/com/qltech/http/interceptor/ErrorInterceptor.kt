package com.qltech.http.interceptor

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.qltech.http.configure.ResponseCode
import com.qltech.http.model.exception.AccessDeniedException
import com.qltech.http.model.exception.ClientUnauthorizedException
import com.qltech.http.model.exception.ServerException
import com.qltech.http.extensions.getBody
import com.qltech.http.model.BaseResponse
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

class ErrorInterceptor : Interceptor {

    private val gSon: Gson = Gson()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val response = chain.proceed(request)
        val statusCode = response.code()
        val data = responseToData(response)

        if (statusCode == ResponseCode.ERROR_CLIENT_UNAUTHORIZED) {
            throw ClientUnauthorizedException(data)
        } else if (statusCode in ResponseCode.ERROR_SERVER_INTERNAL until ResponseCode.ERROR_UNKNOWN) {
            throw if (statusCode == ResponseCode.ERROR_ACCESS_DENIED) {
                AccessDeniedException(data)
            } else {
                ServerException(statusCode, data)
            }
        }

        return response
    }

    private fun responseToData(response: Response): BaseResponse<*> {
        val jsonString = response.getBody()
        return try {
            gSon.fromJson(jsonString, BaseResponse::class.java)
        } catch (e: JsonSyntaxException) {
            BaseResponse<Any>(message = jsonString)
        } catch (e: IllegalStateException) {
            BaseResponse<Any>(message = jsonString)
        } catch (e: NullPointerException) {
            BaseResponse<Any>(message = jsonString)
        }
    }
}
