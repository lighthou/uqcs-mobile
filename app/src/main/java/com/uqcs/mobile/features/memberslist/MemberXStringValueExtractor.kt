package com.uqcs.mobile.features.memberslist

import com.sortabletableview.recyclerview.toolkit.SimpleTableDataColumnAdapter
import com.uqcs.mobile.data.classes.Member

class MemberXStringValueExtractor {

    companion object {
        fun forFirstName(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> { member -> member.firstName }
        }

        fun forLastName(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> { member -> member.lastName }
        }

        fun forEmail(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> { member -> member.email }
        }

        fun forPaid(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> { member -> member.paid.toString() }
        }
    }
}