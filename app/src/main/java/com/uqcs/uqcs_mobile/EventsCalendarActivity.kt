package com.uqcs.uqcs_mobile

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.activity_events_calendar.*
import com.prolificinteractive.materialcalendarview.CalendarDay
import android.support.annotation.NonNull
import android.util.EventLog
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.LocalDate


class EventsCalendarActivity : AppCompatActivity(), OnDateSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_calendar)



        calendarView.setOnDateChangedListener(this)

        var calendarDays : MutableList<CalendarDay> = mutableListOf<CalendarDay>()
        calendarDays.add(CalendarDay.from(LocalDate.now()))
        calendarView.addDecorator(EventDecorator(Color.BLACK, calendarDays))
    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        //If you change a decorate, you need to invalidate decorators

        widget.invalidateDecorators()
    }
}
