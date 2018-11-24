package com.uqcs.mobile

import android.R.attr.data


import com.sortabletableview.recyclerview.toolkit.FilterHelper

internal class MemberFilter(query: String) : FilterHelper.Filter<Member> {

    private val query: String = query.toLowerCase()

    override fun apply(member: Member): Boolean {
        // search for airline name

        if (member.firstName.toLowerCase().contains(query)) {
            return true
        } else if (member.lastName.toLowerCase().contains(query)) {
            return true
        } else if (member.email.toLowerCase().contains(query)) {
            return true
        } else if (member.paid.toString().contains(query)) {
            return true
        }
        return false
    }
}