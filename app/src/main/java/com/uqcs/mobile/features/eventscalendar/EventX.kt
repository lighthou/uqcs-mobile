package com.uqcs.mobile.features.eventscalendar

import com.google.gson.annotations.SerializedName

class EventX (
    @SerializedName("description") val description : String,
    @SerializedName("location") val location : String,
    @SerializedName("start") val start : Start,
    @SerializedName("summary") val summary : String
)

data class Start (
    @SerializedName("dateTime") val dateTime : String
)

