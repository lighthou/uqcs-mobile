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
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val DOCUMENTATION_URL = "http://www.ryankurz.me/docs"

class DocumentationViewModel : ViewModel() {

    private lateinit var documentationStore : DocumentationStore
    private lateinit var HTTPAuth : String
    private lateinit var  webserver: Webserver

    lateinit var listItems : MutableLiveData<List<String>>


    private var screenState : DocumentationState = LIST
    private var keyState = mutableListOf<String>()
    var showLoading : MutableLiveData<Boolean> = MutableLiveData()

    fun getDocumentationFromServer() {
        showLoading.postValue(true)
        val documentationRequests : Call<ResponseBody> = webserver.fetchDocumentation()

        documentationRequests.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                documentationStore = DocumentationStore(JSONObject( response.body()?.string()))
                showLoading.postValue(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("ReturnValue", t.toString())

                showLoading.postValue(false)
            }

        })
    }

    fun registerCredentials(username : String, password : String) {
        webserver = ServiceGenerator.createService(Webserver::class.java, username, password)
    }






}