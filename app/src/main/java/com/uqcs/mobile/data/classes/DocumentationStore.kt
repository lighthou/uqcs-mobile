package com.uqcs.mobile.data.classes

import org.json.JSONObject


class DocumentationStore(private val documentationDictionary : JSONObject) {

    fun getFileTextByKeys(keys : List<String>) : String {
        var tempDictionary : JSONObject = documentationDictionary
        for (i in 0 until keys.size - 1) {
            tempDictionary = tempDictionary.getJSONObject(keys[i])
        }
        return keys[keys.size - 1]
    }

    fun getListByKeys(keys : List<String>) : List<String> {
        var tempDictionary : JSONObject = documentationDictionary
        for (i in 0 until keys.size) {
            tempDictionary = tempDictionary.getJSONObject(keys[i])
        }
        return tempDictionary.keys() as List<String>
    }

    fun getFileByName(filename : String) {
        getFileHelper(documentationDictionary, filename, mutableListOf())
    }

    private fun getFileHelper(tempDictionary: JSONObject, filename: String, keyState : MutableList<String>) : Pair<List<String>, String>? {
        for (key : String in tempDictionary.keys()) {
            val newKeyState = mutableListOf<String>()
            newKeyState.addAll(keyState)
            newKeyState.add(key)
            if (filename == key) {
                return Pair(keyState, tempDictionary.getString(key))
            }
            if (!key.endsWith(".md")) {
                val ret = getFileHelper(tempDictionary.getJSONObject(key), filename, newKeyState)
                if (ret != null) return ret
            }
        }
        return null
    }

    fun getListByDirectoryName(directoryName: String) {
        getListHelper(documentationDictionary, directoryName, mutableListOf())
    }

    private fun getListHelper(tempDictionary: JSONObject, directoryName: String, keyState : MutableList<String>) : Pair<List<String>, List<String>>? {
        for (key : String in tempDictionary.keys()) {
            val newKeyState = mutableListOf<String>()
            newKeyState.addAll(keyState)
            newKeyState.add(key)
            if (directoryName == key) {
                val listData = (tempDictionary[key] as JSONObject).keys() as List<String>
                return Pair(keyState, listData)
            }
            if (!key.endsWith(".md")) {
                val ret = getListHelper(tempDictionary.getJSONObject(key), directoryName, newKeyState)
                if (ret != null) return ret
            }
        }
        return null
    }

    fun getListBySearchQuery(queryText: String) {
        return searchQueryHelper(documentationDictionary, mutableListOf(), queryText)
    }

    private fun searchQueryHelper(currentDic : JSONObject, returnList: MutableList<String>, queryText : String) {
        for (key : String in currentDic.keys()) {
            if (queryText in key) returnList.add(key)
            if (!key.endsWith(".md")) {
                searchQueryHelper(currentDic.getJSONObject(key), returnList, queryText)
            }
        }
    }
}