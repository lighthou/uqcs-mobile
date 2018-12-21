package com.uqcs.mobile


import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uqcs.mobile.data.classes.DocumentationState
import com.uqcs.mobile.data.classes.DocumentationState.*

import com.uqcs.mobile.data.classes.DocumentationStore
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val DOCUMENTATION_URL = "http://www.ryankurz.me/docs"

class DocumentationViewModel : ViewModel() {

    private lateinit var documentationStore : DocumentationStore
    private lateinit var webserver: Webserver

    var listItems : MutableLiveData<List<String>> = MutableLiveData(listOf())
    var textData : MutableLiveData<String> = MutableLiveData("")

    var titleText : MutableLiveData<String> = MutableLiveData("")
    var screenState : MutableLiveData<DocumentationState> = MutableLiveData(INITIAL)
    private var stateKeys = mutableListOf<String>()
    var showLoading : MutableLiveData<Boolean> = MutableLiveData()

    fun getDocumentationFromServer() {
        showLoading.value = true
        val documentationRequests : Call<ResponseBody> = webserver.fetchDocumentation()

        documentationRequests.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                documentationStore = DocumentationStore(JSONObject( response.body()?.string()))
                listItems.value = documentationStore.getInitialState()
                showLoading.value = false
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("ReturnValue", t.toString())
                showLoading.setValue(false)
            }

        })
    }

    fun registerCredentials(username : String, password : String) {
        webserver = ServiceGenerator.createService(Webserver::class.java, username, password)
    }

    fun search(queryText : String) {
        val searchList = documentationStore.getListBySearchQuery(queryText)
        listItems.value = searchList
    }

    fun onBackPressed() {
        if (screenState.value == LIST || screenState.value == VIEW_FILE) {
            stateKeys.removeAt(stateKeys.size - 1)
            val newList = documentationStore.getListByKeys(stateKeys)
            listItems.value = newList
        }

        screenState.value = when {
            screenState.value == INITIAL -> INITIAL
            screenState.value == LIST && stateKeys.size == 0 -> INITIAL
            screenState.value == LIST ->  LIST
            screenState.value == VIEW_FILE -> LIST
            screenState.value == EDIT_FILE -> VIEW_FILE
            screenState.value == PREVIEW_FILE -> VIEW_FILE
            else -> LIST
        }

        updateTitleText()
    }

    private fun updateTitleText() {
        if (screenState.value == EDIT_FILE || screenState.value == PREVIEW_FILE) {
            titleText.setValue("Editing...")
        } else {
            val sb = StringBuilder()
            for (key in stateKeys) {
                sb.append(key)
                sb.append('/')
            }
            titleText.setValue(sb.toString())
        }
    }

    fun onListItemSelected(selectedItem : String) {
        stateKeys.add(selectedItem)
        if (selectedItem.endsWith(".md")) {
            screenState.value = VIEW_FILE
            val fileText = documentationStore.getFileTextByKeys(stateKeys)
            textData.setValue(fileText)
        } else {
            screenState.value = LIST
            val newList = documentationStore.getListByKeys(stateKeys)
            listItems.setValue(newList)
        }
        updateTitleText()
    }

    fun setEditMode() {
        screenState.value = EDIT_FILE
        updateTitleText()
    }

    fun setPreviewMode() {
        screenState.value = PREVIEW_FILE
        updateTitleText()

    }






}