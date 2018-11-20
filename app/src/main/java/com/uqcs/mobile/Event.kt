package com.uqcs.mobile

import java.text.SimpleDateFormat
import java.util.*

class Event constructor(date: String, var description: String, var summary: String, var location: String) {
    var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.UK)
    var date : Date = format.parse(date)
}