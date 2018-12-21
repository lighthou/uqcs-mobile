package com.uqcs.mobile.features.memberslist

import com.uqcs.mobile.MemberX
import com.uqcs.mobile.data.classes.Member

class MemberXComparator {

    companion object {
        fun forFirstName(): Comparator<MemberX> {
            return Comparator<MemberX> { member1, member2 -> member1.first_name.compareTo(member2.first_name) }
        }

        fun forLastName(): Comparator<MemberX> {
            return Comparator<MemberX> { member1, member2 -> member1.last_name.compareTo(member2.last_name) }
        }

        fun forEmail(): Comparator<MemberX> {
            return Comparator<MemberX> { member1, member2 -> member1.email.compareTo(member2.email) }
        }

        fun forPaid(): Comparator<MemberX> {
            return Comparator<MemberX> { member1, member2 -> member1.paid.compareTo(member2.paid) }
        }
    }
}