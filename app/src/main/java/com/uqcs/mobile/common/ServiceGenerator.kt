package com.uqcs.mobile.common

import android.text.TextUtils
import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient


object ServiceGenerator {

    private const val API_BASE_URL = "http://www.ryankurz.me"

    private val httpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private var retrofit = builder.build()

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null, null)
    }

    fun <S> createService(
        serviceClass: Class<S>, username: String?, password: String?
    ): S {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            val authToken : String = Credentials.basic(username!!, password!!)
            return createService(serviceClass, authToken)
        }

        return createService(serviceClass, null)
    }

    fun <S> createService(
        serviceClass: Class<S>, authToken: String?
    ): S {
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = AuthenticationInterceptor(authToken!!)

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)

                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }

        return retrofit.create(serviceClass)
    }
}