package com.uqcs.mobile.features.eventscalendar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uqcs.mobile.MemberX
import com.uqcs.mobile.ServiceGenerator
import com.uqcs.mobile.Webserver
import com.uqcs.mobile.common.AuthenticatedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsCalendarViewModel : ViewModel(), AuthenticatedViewModel {

    private lateinit var webserver: Webserver

    var showLoading : MutableLiveData<Boolean> = MutableLiveData()
    var eventsList : MutableLiveData<List<EventX>> = MutableLiveData()

    fun getEventsListFromServer() { //TODO Should this call be in the viewmodel or the view
        showLoading.value = true
        val eventsRequest : Call<List<EventX>> = webserver.fetchEvents()

        eventsRequest.enqueue(object : Callback<List<EventX>> {
            override fun onResponse(call: Call<List<EventX>>, response: Response<List<EventX>>) {
                eventsList.value = response.body()
                showLoading.value = false
            }

            override fun onFailure(call: Call<List<EventX>>, t: Throwable) {
                Log.i("Retrofit Error", t.toString())
                showLoading.value = false
            }
        })
    }

    override fun registerCredentials(username : String, password : String) {
        webserver = ServiceGenerator.createService(Webserver::class.java, username, password)
    }
}