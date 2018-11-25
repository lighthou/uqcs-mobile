package com.uqcs.mobile

import com.sortabletableview.recyclerview.toolkit.SimpleTableDataColumnAdapter

class MemberStringValueExtractor {
    init {

    }

    companion object {
        fun forFirstName(): SimpleTableDataColumnAdapter.StringValueExtractor<Member> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<Member> { member -> member.firstName }
        }

        fun forLastName(): SimpleTableDataColumnAdapter.StringValueExtractor<Member> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<Member> { member -> member.lastName }
        }

        fun forEmail(): SimpleTableDataColumnAdapter.StringValueExtractor<Member> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<Member> { member -> member.email }
        }

        fun forPaid(): SimpleTableDataColumnAdapter.StringValueExtractor<Member> {
            return SimpleTableDataColumnAdapter.StringValueExtractor<Member> { member -> member.paid.toString() }
        }
    }
}