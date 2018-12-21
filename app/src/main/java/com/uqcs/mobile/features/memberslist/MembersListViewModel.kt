package com.uqcs.mobile.features.memberslist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uqcs.mobile.MemberX
import com.uqcs.mobile.ServiceGenerator
import com.uqcs.mobile.Webserver
import com.uqcs.mobile.common.AuthenticatedViewModel
import com.uqcs.mobile.data.classes.DocumentationState
import com.uqcs.mobile.features.documentation.DocumentationStore
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MembersListViewModel : ViewModel(), AuthenticatedViewModel {

    private lateinit var webserver: Webserver

    private var stateKeys = mutableListOf<String>()
    var showLoading : MutableLiveData<Boolean> = MutableLiveData()
    var membersList : MutableLiveData<List<MemberX>> = MutableLiveData()

    fun getMembersListFromServer() { //TODO Should this be in the viewmodel or the view
        showLoading.value = true
        val membersRequest : Call<List<MemberX>> = webserver.fetchMembers()

        membersRequest.enqueue(object : Callback<List<MemberX>> {
            override fun onResponse(call: Call<List<MemberX>>, response: Response<List<MemberX>>) {
                showLoading.value = false
            }

            override fun onFailure(call: Call<List<MemberX>>, t: Throwable) {
                Log.i("ReturnValue", t.toString())
                showLoading.value = false
            }

        })
    }

    override fun registerCredentials(username : String, password : String) {
        webserver = ServiceGenerator.createService(Webserver::class.java, username, password)
    }
}