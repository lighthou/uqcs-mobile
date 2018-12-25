package com.uqcs.mobile.features.eventscalendar

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.uqcs.mobile.Helpers.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import kotlinx.android.synthetic.main.activity_events_calendar.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.threeten.bp.LocalDate


class EventsCalendarFragment : Fragment(), AuthenticatedFragment, OnDateSelectedListener {

    private lateinit var viewModel : EventsCalendarViewModel
    private var eventsList : MutableList<EventX> = mutableListOf()
    private lateinit var selectedEvent : EventX

    companion object {
        fun newInstance(): EventsCalendarFragment {
            return EventsCalendarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(EventsCalendarViewModel::class.java)

        progress_overlay.loading_text.text = getString(R.string.fetching_members)

        setUpObservers()

        viewModel.getEventsListFromServer() //TODO do here or on viewmodel init?
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.members_list_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView.setOnDateChangedListener(this)
        //Set calendar to current date.
        calendarView.setSelectedDate(LocalDate.now())
        viewModel.initialCalendarSetUp()

        eventDetailsButton.setOnClickListener { v ->
            viewEventDetails(v)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_events_calendar, container, false)
    }

    override fun registerServerCredentials() {
        val username = (context as MainActivity).username
        val password = (context as MainActivity).password
        viewModel.registerCredentials(username, password)
    }

    override fun onDateSelected(widget : MaterialCalendarView, date: CalendarDay, selected : Boolean) {
        viewModel.onDateSelected(date)
    }

    private fun addEventToCalendar(day : CalendarDay) {
        val calendarDays : MutableList<CalendarDay> = mutableListOf()
        calendarDays.add(day)
        calendarView.addDecorator(EventDecorator(calendarDays, context!!))
    }

    private fun viewEventDetails(v : View) {
        val dialog = EventXCalendarDetailsDialog(
            activity as Activity,
            selectedEvent
        )
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun updateUiByDate(date : CalendarDay) {

    }

    private fun setUpObservers() {
        viewModel.eventsList.observe(this, Observer<List<EventX>> { updatedList ->
            this.eventsList.clear()
            this.eventsList.addAll(updatedList)
        })

        viewModel.selectedDate.observe(this, Observer<CalendarDay> { selectedDate ->
            updateUiByDate(selectedDate)
        })

        viewModel.showLoading.observe(this, Observer<Boolean> { shouldShowLoading ->
            if (shouldShowLoading) showLoadingOverlay() else hideLoadingOverlay()
        })
    }

    private fun showLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
    }

    private fun hideLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
    }
}