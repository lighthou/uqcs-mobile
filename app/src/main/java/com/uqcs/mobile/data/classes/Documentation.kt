package com.uqcs.mobile.data.classes

import android.util.Log
import org.json.JSONObject

class Documentation(val docsDictionary : JSONObject) {

    private var state : JSONObject = docsDictionary
    private var stateKeys : MutableList<String> = mutableListOf<String>()
    var screenIsFile = false

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

    fun goBack() : MutableList<String> {
        screenIsFile = false
        if (stateKeys.isEmpty()) return state.get("list") as MutableList<String>
        stateKeys.removeAt(stateKeys.size - 1)
        var currentDic = docsDictionary
        for (i in 0 until stateKeys.size) {
            currentDic = currentDic.getJSONObject(stateKeys[i])
        }
        state = currentDic
        return state.get("list") as MutableList<String>
    }

    fun itemSelected(item : String) : MutableList<String>{
        stateKeys.add(item)
        state = state.getJSONObject(item)
        return state.get("list") as MutableList<String>
    }

    fun fileSelected(file : String) : String {
        screenIsFile = true
        stateKeys.add(file)
        return state.getString(file)
    }

    fun getListState() : MutableList<String> {
        return state.get("list") as MutableList<String>
    }





}