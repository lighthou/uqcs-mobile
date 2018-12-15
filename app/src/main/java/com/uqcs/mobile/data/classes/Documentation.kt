package com.uqcs.mobile.data.classes

import android.util.Log
import android.widget.Toast
import org.json.JSONObject

class Documentation(val docsDictionary : JSONObject) {

    var state : JSONObject = docsDictionary
    var stateKeys : MutableList<String> = mutableListOf<String>()
    var screenIsFile = false
    var isInEditMode = false

    init {
        val keysList : MutableList<String> = mutableListOf()
        for (key : String in docsDictionary.keys()) {
            keysList.add(key)
        }
        docsDictionary.put("list", keysList)
        addListsOfKeys(docsDictionary)
    }

    private fun addListsOfKeys(currentDic : JSONObject) {
        var keysList : MutableList<String> = mutableListOf()
        for (key : String in currentDic.keys()) {
            if (!key.endsWith(".md") && !key.endsWith("list")) {
                val internalDic = currentDic.getJSONObject(key)
                for (second_key : String in internalDic.keys()) {
                    keysList.add(second_key)
                }
                internalDic.put("list", keysList)
                keysList = mutableListOf()
                addListsOfKeys(currentDic.getJSONObject(key))
            }
        }
    }

    fun goBack() {
        screenIsFile = false
        if (stateKeys.isEmpty()) return
        stateKeys.removeAt(stateKeys.size - 1)
        var currentDic = docsDictionary
        for (i in 0 until stateKeys.size) {
            currentDic = currentDic.getJSONObject(stateKeys[i])
        }
        state = currentDic
    }

    fun itemSelected(item : String) : MutableList<String>{
        if (state.has(item)) {
            stateKeys.add(item)
            state = state.getJSONObject(item)
            return state.get("list") as MutableList<String>
        }
        return findItemAndUpdateStates(docsDictionary, item, mutableListOf())
    }

    fun fileSelected(file : String) : String {
        screenIsFile = true
        if (state.has(file)) {
            stateKeys.add(file)
            return state.getString(file)
        }
        return findFileAndUpdateStates(docsDictionary, file, mutableListOf())
    }

    private fun findFileAndUpdateStates(currentDic: JSONObject, item: String, states : MutableList<String>) : String {
        for (key : String in currentDic.keys()) {
            val newList = mutableListOf<String>()
            newList.addAll(states)
            newList.add(key)
            if (item == key) {
                state = currentDic
                stateKeys = newList
                return state.getString(key)
            }
            if (!key.endsWith(".md") && !key.endsWith("list")) {
                val ret = findFileAndUpdateStates(currentDic.getJSONObject(key), item, newList)
                if (ret != "") return ret
            }
        }
        return ""
    }

    private fun findItemAndUpdateStates(currentDic: JSONObject, item: String, states : MutableList<String>) : MutableList<String>{
        for (key : String in currentDic.keys()) {
            val newList = mutableListOf<String>()
            newList.addAll(states)
            newList.add(key)
            if (item == key) {
                state = currentDic[key] as JSONObject
                stateKeys = newList


                return state.get("list") as MutableList<String>
            }
            if (!key.endsWith(".md") && !key.endsWith("list")) {
                val ret = findItemAndUpdateStates(currentDic.getJSONObject(key), item, newList)
                if (!ret.isEmpty()) return ret
            }
        }
        return mutableListOf()
    }



    fun getListState() : MutableList<String> {
        return state.get("list") as MutableList<String>
    }

    fun search(query : String)  : MutableList<String> {
        val returnList = mutableListOf<String>()
        listBySearchQuery(docsDictionary, returnList, query)
        return returnList
    }

    private fun listBySearchQuery(currentDic : JSONObject, returnList: MutableList<String>, query : String) {
        for (key : String in currentDic.keys()) {
            if (query in key && key != "list") returnList.add(key)
            if (!key.endsWith(".md") && !key.endsWith("list")) {
                listBySearchQuery(currentDic.getJSONObject(key), returnList, query)
            }
        }
    }




}