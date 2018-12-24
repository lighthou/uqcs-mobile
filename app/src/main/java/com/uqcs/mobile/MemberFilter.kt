package com.uqcs.mobile

import com.sortabletableview.recyclerview.toolkit.FilterHelper
import com.uqcs.mobile.data.classes.Member

internal class MemberFilter(private val query: String) : FilterHelper.Filter<Member> {

    override fun apply(member: Member): Boolean {
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