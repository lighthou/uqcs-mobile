package com.uqcs.mobile

import com.google.gson.annotations.SerializedName

data class EventX (
    @SerializedName("description") val email : String,
    @SerializedName("location") val first_name : String,
    @SerializedName("start") val last_name : Start,
    @SerializedName("summary") val paid : Boolean
)

data class Start (
    @SerializedName("dateTime") val dateTime : String
)

