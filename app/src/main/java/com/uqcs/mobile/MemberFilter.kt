package com.uqcs.mobile

import android.util.Log
import com.sortabletableview.recyclerview.toolkit.FilterHelper

internal class MemberFilter(private val query: String) : FilterHelper.Filter<Member> {

    init {
        Log.i("NOT_WORKING", "AH")
    }
    override fun apply(member: Member): Boolean {
        Log.i("WORKING", "AH")
        val queryLowerCase = query.toLowerCase()
        return when {
            member.firstName.toLowerCase().contains(queryLowerCase) -> true
            member.lastName.toLowerCase().contains(queryLowerCase) -> true
            member.email.toLowerCase().contains(queryLowerCase) -> true
            member.paid.toString().contains(queryLowerCase) -> true
            else -> false
        }
    }
}