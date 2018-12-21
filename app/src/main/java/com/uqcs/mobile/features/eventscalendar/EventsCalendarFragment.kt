package com.uqcs.mobile.features.eventscalendar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*


class EventsCalendarFragment : Fragment(), AuthenticatedFragment {

    private lateinit var viewModel : EventsCalendarViewModel
    private var eventsList : MutableList<EventX> = mutableListOf()

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun registerServerCredentials() {
        val username = (context as MainActivity).username
        val password = (context as MainActivity).password
        viewModel.registerCredentials(username, password)
    }

    private fun setUpObservers() {
        viewModel.eventsList.observe(this, Observer<List<EventX>> { updatedList ->
            this.eventsList.clear()
            this.eventsList.addAll(updatedList)
        })
    }



}