package com.uqcs.uqcs_mobile

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.activity_events_calendar.*
import com.prolificinteractive.materialcalendarview.CalendarDay
import android.support.annotation.NonNull
import android.util.EventLog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONArray
import org.json.JSONObject
import org.threeten.bp.LocalDate
import java.util.*

class EventsCalendarActivity : AppCompatActivity(), OnDateSelectedListener {

    private var currentlySelectedDate : CalendarDay? = null
    private var requestQueue: RequestQueue? = null
    private val EVENTS_URL = "http://www.ryankurz.me/events"
    private var eventsMap : MutableMap<CalendarDay, Event> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_calendar)

        progress_overlay.loading_text.text = "Fetching Events..."
        Util.animateView(this, progress_overlay, View.VISIBLE, 0.8f, 200)

        calendarView.setOnDateChangedListener(this)
        //Set calendar to current date.
        calendarView.setSelectedDate(LocalDate.now())
        val c : Calendar = Calendar.getInstance()
        currentlySelectedDate = CalendarDay.from(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        onDateSelected(calendarView, currentlySelectedDate as CalendarDay, true)

        requestQueue = Volley.newRequestQueue(this)
        requestQueue?.add(getCalendarEventsRequest())
    }

    private fun getCalendarEventsRequest() : JsonArrayRequest {

        return JsonArrayRequest(Request.Method.GET, EVENTS_URL, null,
            Response.Listener<JSONArray> { response ->
                val responseString = response.toString()
                val events = JSONArray(responseString)
                for (i in 0..(events.length() - 1)) {
                    val event: JSONObject = events.getJSONObject(i)
                    val eventDate : String = if (event.has("start") && event.getJSONObject("start").has("dateTime"))  event.getJSONObject("start").getString("dateTime") else ""
                    val eventDescription : String = if (event.has("description")) event.getString("description") else ""
                    val eventSummary : String = if (event.has("summary")) event.getString("summary") else ""
                    val eventLocation : String = if (event.has("location")) event.getString("location") else ""

                    if (eventDate != "") {
                        val tempEvent: Event = Event(eventDate, eventDescription, eventSummary, eventLocation)
                        addEventToCalendar(calendarDayFromEvent(tempEvent))
                        eventsMap[calendarDayFromEvent(tempEvent)] = tempEvent
                    }

                }
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
            })
    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        // CalendarDay uses a 1-12 day whereas monthNumberToName indexes 0-11 so we subtract one.
        dateText.text = resources.getString(R.string.date_format, Util.monthNumberToName(date.month - 1), date.day,date.year)
        if (eventsMap.containsKey(date)) {
            eventName.text = eventsMap[date]?.summary
            eventDetailsButton.visibility = View.VISIBLE
        } else {
            eventName.text = "No Events"
            eventDetailsButton.visibility = View.GONE
        }
        currentlySelectedDate = date

        //If you change a decorate, you need to invalidate decorators
        widget.invalidateDecorators()
    }

    fun calendarDayFromEvent(event: Event) : CalendarDay {
        val date: Date = event.date // your date
        val cal = Calendar.getInstance()
        cal.time = date
        return CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }


    fun addEventToCalendar(day : CalendarDay) {
        val calendarDays : MutableList<CalendarDay> = mutableListOf<CalendarDay>()
        calendarDays.add(day)
        calendarView.addDecorator(EventDecorator(Color.BLACK, calendarDays, this))
    }

    fun viewEventDetails(v : View) {
        EventDetailsDialog(this, eventsMap[currentlySelectedDate] as Event).show()
    }


}
