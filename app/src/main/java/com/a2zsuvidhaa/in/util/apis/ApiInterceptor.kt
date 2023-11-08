package com.a2zsuvidhaa.`in`.util.apis

import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.util.AppLog
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class ApiInterceptor @Inject constructor(
        private val appPreference: AppPreference,
        private val networkConnection: NetworkConnection
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        //Checking is network available
        networkInterceptor()

        //Header Interceptor
        //for Injecting authorization bearer token
        //at runtime  etc...
        val request = headerInterceptor(chain)


        //Intercepting response and checking errors
        val mainResponse: Response = chain.proceed(request)
        when (mainResponse.code) {
            200, 201 -> return mainResponse
            401 -> throw Exceptions.UnAuthorizedException("User UnAuthorized - token expired or may you have logged " +
                    "in another device. code ${mainResponse.code}")
            500 -> throw Exceptions.InternalServerError("Internal server error, it will resolve soon")
            else -> throw Exceptions.InternalServerError("Server Error( ${mainResponse.message})-${mainResponse.code}")
        }
    }


    private fun headerInterceptor(chain: Interceptor.Chain): Request {
        var request = chain.request()
        request = request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("user-id", appPreference.id.toString())
                .addHeader("token",appPreference.token)
                .build()

        AppLog.d("Header ${request.toString()}")
        return request
    }

    private fun networkInterceptor() {
        if (!networkConnection.isInternetAvailable())
            throw Exceptions.NoInternetException("No Internet connection available")
    }
}




