package com.uqcs.uqcs_mobile

import java.text.SimpleDateFormat
import java.util.*

class Util {
    companion object {
        fun monthNumberToName(monthNumber : Int): String {
            var calendar : Calendar = Calendar.getInstance()
            var month_date : SimpleDateFormat = SimpleDateFormat("MMMM")
            calendar.set(Calendar.MONTH, monthNumber)
            return month_date.format(calendar.time)
        }
    }

}
