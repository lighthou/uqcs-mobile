package com.uqcs.mobile.features.memberslist

import com.sortabletableview.recyclerview.toolkit.SimpleTableDataColumnAdapter

class MemberXStringValueExtractor {

    companion object {
        fun forFirstName(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor { member -> member.firstName }
        }

        fun forLastName(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor { member -> member.lastName }
        }

        fun forEmail(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor { member -> member.email }
        }

        fun forPaid(): SimpleTableDataColumnAdapter.StringValueExtractor<MemberX> {
            return SimpleTableDataColumnAdapter.StringValueExtractor { member -> member.paid.toString() }
        }
    }
}