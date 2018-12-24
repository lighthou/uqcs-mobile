package com.uqcs.mobile.features.eventscalendar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.uqcs.mobile.R


class EventDecorator(dates: Collection<CalendarDay>, private val context: Context) : DayViewDecorator {
    private val dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        val uqcsDrawable : Drawable = ContextCompat.getDrawable(context, R.drawable.uqcs_logo_no_bg) as Drawable
        view.setBackgroundDrawable(uqcsDrawable)
    }
}