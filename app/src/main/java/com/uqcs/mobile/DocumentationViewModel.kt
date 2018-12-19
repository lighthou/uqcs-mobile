package com.uqcs.mobile


import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uqcs.mobile.data.classes.DocumentationState
import com.uqcs.mobile.data.classes.DocumentationState.*
import com.uqcs.mobile.data.classes.Documentation
import com.uqcs.mobile.data.classes.DocumentationStore
import kotlinx.android.synthetic.main.activity_documentation.*
import org.json.JSONObject
import retrofit2.Retrofit


class DocumentationViewModel(application: Application) : AndroidViewModel(application) {

    private val DOCUMENTATION_URL = "http://www.ryankurz.me/docs"
    private lateinit var documentationStore : DocumentationStore
    private var screenState : DocumentationState = LIST
    private var keyState = mutableListOf<String>()
    private lateinit var HTTPAuth : String

    lateinit var observableListItems : List<String>
    var webserver: Webserver = RetrofitClientInstance.retrofitInstance.create(Webserver::class.java)

    fun getDocumentationFromServer() {
        var ex : List<EventX> = webserver.events()
    }

    fun registerCredentials(username : String, password : String) {
        val userAndPassword = "$username:$password"
        HTTPAuth = "Basic " + Base64.encodeToString(userAndPassword.toByteArray(), Base64.NO_WRAP)
    }

    private fun getDocumentationRequest() : JsonObjectRequest {

        return object : JsonObjectRequest(
            Request.Method.GET, DOCUMENTATION_URL, null,
            Response.Listener<JSONObject> { response ->
                val docs = JSONObject(response.toString())
                documentationStore = DocumentationStore(docs)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = mutableMapOf<String, String>()
                params["Authorization"] = HTTPAuth
                return params
            }
        }
    }






}