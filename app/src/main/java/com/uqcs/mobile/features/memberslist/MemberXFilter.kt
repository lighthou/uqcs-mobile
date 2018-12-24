package com.uqcs.mobile.features.memberslist

import com.sortabletableview.recyclerview.toolkit.FilterHelper

internal class MemberXFilter(private val query: String) : FilterHelper.Filter<MemberX> {

    override fun apply(member: MemberX): Boolean {
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