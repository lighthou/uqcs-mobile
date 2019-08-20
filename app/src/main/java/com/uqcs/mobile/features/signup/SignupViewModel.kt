package com.uqcs.mobile.features.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uqcs.mobile.common.AuthenticatedViewModel
import com.uqcs.mobile.common.ServiceGenerator
import com.uqcs.mobile.common.Webserver
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel(), AuthenticatedViewModel {
    private lateinit var webserver: Webserver

    var showLoading : MutableLiveData<Boolean> = MutableLiveData()

    override fun registerCredentials(username: String, password: String) {
        webserver = ServiceGenerator.createService(Webserver::class.java, username, password)
    }

    fun sendMemberDetails(image: String) {
        val eventsRequest : Call<ResponseBody> = webserver.createMember(image)

        eventsRequest.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading.value = false
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("Retrofit Error", t.toString())
                showLoading.value = false
            }
        })
    }

}