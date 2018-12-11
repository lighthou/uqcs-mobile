package com.uqcs.mobile.data.classes

import org.json.JSONObject

class Documentation(var docsDictionary : JSONObject) {

    private var state : JSONObject = docsDictionary

    init {
        val keysList : MutableList<String> = mutableListOf()
        for (key : String in docsDictionary.keys()) {
            keysList.add(key)
        }
        docsDictionary.put("list", keysList)
        addListsOfKeys(docsDictionary)
    }

    private fun addListsOfKeys(currentDic : JSONObject) {
        val keysList : MutableList<String> = mutableListOf()
        for (key : String in currentDic.keys()) {
            if (key.endsWith(".md") || key.endsWith("list")) {
                continue
            }
            val internalDic = currentDic.getJSONObject(key)
            for (second_key : String in internalDic.keys()) {
                keysList.add(second_key)
            }
            internalDic.put("list", keysList)
            addListsOfKeys(currentDic.getJSONObject(key))
        }
    }

    fun itemSelected(item : String) : MutableList<String>{
        state = state.getJSONObject(item)
        return state.get("list") as MutableList<String>
    }

    fun fileSelected(file : String) : String {
        return state.getString(file)
    }

    fun getListState() : MutableList<String> {
        return state.get("list") as MutableList<String>
    }





}