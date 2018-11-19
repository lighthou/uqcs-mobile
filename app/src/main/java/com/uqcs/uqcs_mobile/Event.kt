package com.uqcs.uqcs_mobile

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class Event constructor(date: String, var description: String, var summary: String, var location: String) {
    var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    var date : Date = format.parse(date)
}