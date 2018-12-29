package com.uqcs.mobile.features.eventscalendar

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import com.uqcs.mobile.common.Util
import kotlinx.android.synthetic.main.activity_events_calendar.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.threeten.bp.LocalDate


class EventsCalendarFragment : Fragment(), AuthenticatedFragment, OnDateSelectedListener {

    private lateinit var viewModel : EventsCalendarViewModel
    private var eventsList : MutableList<EventX> = mutableListOf()
    private var selectedEvent : EventX? = null
    private var displayedText : String = ""

    companion object {
        fun newInstance(): EventsCalendarFragment {
            return EventsCalendarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(EventsCalendarViewModel::class.java)


        setUpObservers()
        registerServerCredentials()

        viewModel.getEventsListFromServer() //TODO do here or on viewmodel init?
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.events_calendar_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_event -> {
                Toast.makeText(context!!, "Not implemented", Toast.LENGTH_LONG).show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_overlay.loading_text.text = getString(R.string.fetching_events)

        events_calendar_toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(events_calendar_toolbar)

        calendarView.setOnDateChangedListener(this)
        //Set calendar to current date.
        calendarView.setSelectedDate(LocalDate.now())
        viewModel.initialCalendarSetUp()

        eventDetailsButton.setOnClickListener {
            showEventDetailsDialog()
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

    private fun showEventDetailsDialog() {
        val dialog = EventXCalendarDetailsDialog(
            activity!!,
            selectedEvent!!
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun updateUiByDate(date : CalendarDay) {
        dateText.text = resources.getString(
            R.string.date_format,
            Util.monthNumberToName(date.month - 1), date.day,date.year)
        if (selectedEvent != null) {
            eventName.text = displayedText
            events_calendar_toolbar.title = displayedText
            eventDetailsButton.visibility = View.VISIBLE
        } else {
            eventName.text = getString(R.string.no_events)
            events_calendar_toolbar.title = getString(R.string.no_events)
            eventDetailsButton.visibility = View.GONE
        }
    }

    private fun setUpObservers() {
        viewModel.eventsList.observe(this, Observer<List<EventX>> { updatedList ->
            this.eventsList.clear()
            this.eventsList.addAll(updatedList)
        })

        viewModel.selectedDate.observe(this, Observer<CalendarDay> { selectedDate ->
            updateUiByDate(selectedDate)
        })

        viewModel.selectedEvent.observe(this, Observer { selectedEvent ->
            this.selectedEvent = selectedEvent
        })

        viewModel.showLoading.observe(this, Observer<Boolean> { shouldShowLoading ->
            if (shouldShowLoading) showLoadingOverlay() else hideLoadingOverlay()
        })

        viewModel.displayedText.observe(this, Observer<String> { text ->
            this.displayedText = text
        })

        viewModel.datesToDecorate.observe(this, Observer<Set<CalendarDay>> { days ->
            calendarView.addDecorator(EventDecorator(days, context!!))
            calendarView.invalidateDecorators()
        })
    }

    private fun showLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
    }

    private fun hideLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
    }
}