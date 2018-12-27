package com.uqcs.mobile.features.eventscalendar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.uqcs.mobile.common.ServiceGenerator
import com.uqcs.mobile.common.Webserver
import com.uqcs.mobile.common.AuthenticatedViewModel
import kotlinx.android.synthetic.main.activity_events_calendar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EventsCalendarViewModel : ViewModel(), AuthenticatedViewModel {

    private lateinit var webserver: Webserver

    var showLoading : MutableLiveData<Boolean> = MutableLiveData()
    var eventsList : MutableLiveData<List<EventX>> = MutableLiveData()
    var selectedDate : MutableLiveData<CalendarDay> = MutableLiveData()
    var selectedEvent : MutableLiveData<EventX> = MutableLiveData()
    var dateToEventStore : MutableMap<CalendarDay, EventX> = mutableMapOf<CalendarDay, EventX>()

    fun getEventsListFromServer() { //TODO Should this call be in the viewmodel or the view
        showLoading.value = true
        val eventsRequest : Call<List<EventX>> = webserver.fetchEvents()

        eventsRequest.enqueue(object : Callback<List<EventX>> {
            override fun onResponse(call: Call<List<EventX>>, response: Response<List<EventX>>) {
                eventsList.value = response.body()
                registerDatesInEventsMap()
                showLoading.value = false
            }

            override fun onFailure(call: Call<List<EventX>>, t: Throwable) {
                Log.i("Retrofit Error", t.toString())
                showLoading.value = false
            }
        })
    }

    override fun registerCredentials(username : String, password : String) {
        webserver = ServiceGenerator.createService(Webserver::class.java, username, password)
    }

    private fun calendarDayFromEvent(event: EventX) : CalendarDay {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.UK)
        val date : Date = format.parse(event.start.dateTime)
        val cal = Calendar.getInstance()
        cal.time = date
        return CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    fun onDateSelected(date : CalendarDay) {
//        dateText.text = resources.getString(
//            R.string.date_format,
//            Util.monthNumberToName(date.month - 1), date.day,date.year)
//        if (eventsMap.containsKey(date)) {
//            eventName.text = eventsMap[date]?.summary
//            eventDetailsButton.visibility = View.VISIBLE
//        } else {
//            eventName.text = getString(R.string.no_events)
//            eventDetailsButton.visibility = View.GONE
//        }
//        currentlySelectedDate = date
    }

    fun initialCalendarSetUp() {
        val c : Calendar = Calendar.getInstance()
        selectedDate.value = CalendarDay.from(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }

    fun registerDatesInEventsMap() {
        for (event : EventX in eventsList.value.orEmpty()) {
            val date = calendarDayFromEvent(event)
            dateToEventStore[date] = event
        }
    }
}