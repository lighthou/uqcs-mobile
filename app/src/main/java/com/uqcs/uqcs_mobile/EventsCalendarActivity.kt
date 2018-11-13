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
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.LocalDate
import java.util.*

class EventsCalendarActivity : AppCompatActivity(), OnDateSelectedListener {

    private var currentlySelectedDate : CalendarDay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_calendar)

        calendarView.setOnDateChangedListener(this)
        //Set calendar to current date.
        calendarView.setSelectedDate(LocalDate.now())
        val c : Calendar = Calendar.getInstance()
        currentlySelectedDate = CalendarDay.from(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dateText.text = resources.getString(R.string.date_format, Util.monthNumberToName(currentlySelectedDate?.month as Int),
            currentlySelectedDate?.day, currentlySelectedDate?.year)

    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        //If you change a decorate, you need to invalidate decorators
        dateText.text = resources.getString(R.string.date_format, Util.monthNumberToName(date.month), date.day,date.year)
        currentlySelectedDate = date
        widget.invalidateDecorators()
    }

    fun addEvent(v : View) {
        val calendarDays : MutableList<CalendarDay> = mutableListOf<CalendarDay>()
        calendarDays.add(currentlySelectedDate as CalendarDay)
        calendarView.addDecorator(EventDecorator(Color.BLACK, calendarDays))
    }


}
