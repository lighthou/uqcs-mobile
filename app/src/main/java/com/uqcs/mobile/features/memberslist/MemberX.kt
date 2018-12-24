package com.uqcs.mobile.features.memberslist

import com.google.gson.annotations.SerializedName

data class MemberX (
     @SerializedName("email") val email : String,
     @SerializedName("first_name") val firstName : String,
     @SerializedName("last_name") val lastName : String,
     @SerializedName("paid") val paid : Boolean
)
