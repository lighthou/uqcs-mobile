package com.uqcs.mobile

class MemberComparator {

    companion object {
        fun forFirstName(): Comparator<Member> {
            return Comparator<Member> { member1, member2 -> member1.firstName.compareTo(member2.firstName) }
        }

        fun forLastName(): Comparator<Member> {
            return Comparator<Member> { member1, member2 -> member1.lastName.compareTo(member2.lastName) }
        }

        fun forEmail(): Comparator<Member> {
            return Comparator<Member> { member1, member2 -> member1.email.compareTo(member2.email) }
        }

        fun forPaid(): Comparator<Member> {
            return Comparator<Member> { member1, member2 -> member1.paid.compareTo(member2.paid) }
        }
    }
}