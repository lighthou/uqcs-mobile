package com.uqcs.mobile.common

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOError

class AuthenticationInterceptor(private val authToken: String) : Interceptor {

    @Throws(IOError::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
            .header("Authorization", authToken)

        val request = builder.build()
        return chain.proceed(request)
    }
}