package com.uqcs.mobile.fragments

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.uqcs.mobile.EventsCalendarDetailsDialog
import com.uqcs.mobile.R
import com.uqcs.mobile.common.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.data.classes.Event
import com.uqcs.mobile.features.eventscalendar.EventDecorator
import kotlinx.android.synthetic.main.activity_events_calendar.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONArray
import org.json.JSONObject
import org.threeten.bp.LocalDate
import java.util.*

class CalendarFragment : Fragment(), OnDateSelectedListener {

    private var currentlySelectedDate : CalendarDay? = null
    private var requestQueue: RequestQueue? = null
    private val EVENTS_URL = "http://www.ryankurz.me/events"
    private var eventsMap : MutableMap<CalendarDay, Event> = mutableMapOf()
    private var username = ""
    private var password = ""

    companion object {

        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_events_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_overlay.loading_text.text = getString(R.string.fetching_events)
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)

        username = (context as MainActivity).username
        password = (context as MainActivity).password
        calendarView.setOnDateChangedListener(this)
        //Set calendar to current date.
        calendarView.setSelectedDate(LocalDate.now())
        val c : Calendar = Calendar.getInstance()
        currentlySelectedDate = CalendarDay.from(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
        onDateSelected(calendarView, currentlySelectedDate as CalendarDay, true)

        eventDetailsButton.setOnClickListener { v ->
            viewEventDetails(v)
        }
        requestQueue = Volley.newRequestQueue(context!!)
        requestQueue?.add(getCalendarEventsRequest())
    }

    private fun getCalendarEventsRequest() : JsonArrayRequest {

        return object : JsonArrayRequest(
            Request.Method.GET, EVENTS_URL, null,
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
                        val tempEvent =
                            Event(eventDate, eventDescription, eventSummary, eventLocation)
                        addEventToCalendar(calendarDayFromEvent(tempEvent))
                        eventsMap[calendarDayFromEvent(tempEvent)] = tempEvent
                    }

                }
                Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(context!!, "Failed to fetch calendar events", Toast.LENGTH_LONG).show()
            })  {
            override fun getHeaders(): Map<String, String> {
                val params = mutableMapOf<String, String>()
                val userAndPassword = "$username:$password"
                val basicAuth = "Basic " + Base64.encodeToString(userAndPassword.toByteArray(), Base64.NO_WRAP)
                params["Authorization"] = basicAuth
                return params
            }
        }
    }


    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        // CalendarDay uses a 1-12 day whereas monthNumberToName indexes 0-11 so we subtract one.
        dateText.text = resources.getString(
            R.string.date_format,
            Util.monthNumberToName(date.month - 1), date.day,date.year)
        if (eventsMap.containsKey(date)) {
            eventName.text = eventsMap[date]?.summary
            eventDetailsButton.visibility = View.VISIBLE
        } else {
            eventName.text = getString(R.string.no_events)
            eventDetailsButton.visibility = View.GONE
        }
        currentlySelectedDate = date

        //If you change a decorate, you need to invalidate decorators
        widget.invalidateDecorators()
    }

    private fun calendarDayFromEvent(event: Event) : CalendarDay {
        val date: Date = event.date // your date
        val cal = Calendar.getInstance()
        cal.time = date
        return CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }


    private fun addEventToCalendar(day : CalendarDay) {
        val calendarDays : MutableList<CalendarDay> = mutableListOf()
        calendarDays.add(day)
        calendarView.addDecorator(EventDecorator(calendarDays, context!!))
    }

    private fun viewEventDetails(v : View) {
        val dialog = EventsCalendarDetailsDialog(
            activity as Activity,
            eventsMap[currentlySelectedDate] as Event
        )
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


}