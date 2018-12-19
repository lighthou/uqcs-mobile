package com.uqcs.mobile

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit


object RetrofitClientInstance {

    private var retrofit: Retrofit? = null
    private const val BASE_URL = "http://www.ryankurz.me"

    val retrofitInstance: Retrofit

        get() {
            if (retrofit == null) {
                retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
}