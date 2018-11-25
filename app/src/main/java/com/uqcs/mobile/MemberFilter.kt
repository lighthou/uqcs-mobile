package com.uqcs.mobile


import android.util.Log
import com.sortabletableview.recyclerview.toolkit.FilterHelper

internal class MemberFilter(query: String) : FilterHelper.Filter<Member> {

    private val query: String = query.toLowerCase()

    override fun apply(member: Member): Boolean {
        // search for airline name
        Log.i("KKKK", "ok")
        return when {
            member.firstName.toLowerCase().contains(query) -> true
            member.lastName.toLowerCase().contains(query) -> true
            member.email.toLowerCase().contains(query) -> true
            member.paid.toString().contains(query) -> true
            else -> false
        }
    }
}