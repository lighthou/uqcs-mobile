package com.uqcs.mobile.features.memberslist

class MemberXComparator {

    companion object {
        fun forFirstName(): Comparator<MemberX> {
            return Comparator { member1, member2 -> member1.firstName.compareTo(member2.firstName) }
        }

        fun forLastName(): Comparator<MemberX> {
            return Comparator { member1, member2 -> member1.lastName.compareTo(member2.lastName) }
        }

        fun forEmail(): Comparator<MemberX> {
            return Comparator { member1, member2 -> member1.email.compareTo(member2.email) }
        }

        fun forPaid(): Comparator<MemberX> {
            return Comparator { member1, member2 -> member1.paid.compareTo(member2.paid) }
        }
    }
}