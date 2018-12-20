package com.uqcs.mobile


import android.app.Application
import android.app.Service
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uqcs.mobile.data.classes.DocumentationState
import com.uqcs.mobile.data.classes.DocumentationState.LIST
import com.uqcs.mobile.data.classes.DocumentationStore
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DocumentationViewModel : ViewModel() {

    private val DOCUMENTATION_URL = "http://www.ryankurz.me/docs"
    private lateinit var documentationStore : DocumentationStore
    private var screenState : DocumentationState = LIST
    private var keyState = mutableListOf<String>()
    private lateinit var HTTPAuth : String

    lateinit var observableListItems : List<String>
    lateinit var  webserver: Webserver
    var showLoading : MutableLiveData<Boolean> = MutableLiveData(false)

    fun getDocumentationFromServer() {
        showLoading.postValue(true)
        val ex : Call<List<EventX>> = webserver.fetchEvents()
        ex.enqueue(object : Callback<List<EventX>> {

            override fun onResponse(call: Call<List<EventX>>, response: Response<List<EventX>>) {
                var eventList : List<EventX> = response.body()!!
                Log.i("Hello", "there")

                showLoading.postValue(false)

            }

            override fun onFailure(call: Call<List<EventX>>, t: Throwable) {
                Log.i("HTTPError", t.toString())
                showLoading.postValue(false)

                //Toast.makeText(this@MainActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun registerCredentials(username : String, password : String) {
        webserver = ServiceGenerator.createService(Webserver::class.java, username, password)
    }






}