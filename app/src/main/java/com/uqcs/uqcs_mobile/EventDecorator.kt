package com.uqcs.uqcs_mobile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import android.R.attr.bitmap




class EventDecorator(private val color: Int, dates: Collection<CalendarDay>, private val context: Context) : DayViewDecorator {
    private val dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        val uqcsDrawable : Drawable = ContextCompat.getDrawable(context, R.drawable.uqcs_logo_no_bg) as Drawable
        view.setBackgroundDrawable(uqcsDrawable)
    }
}