package com.uqcs.mobile

import com.google.gson.annotations.SerializedName

data class MemberX (
     @SerializedName("email") val email : String,
     @SerializedName("first_name") val first_name : String,
     @SerializedName("last_name") val last_name : String,
     @SerializedName("paid") val paid : Boolean
)
