package com.uqcs.mobile.features.eventscalendar

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Window
import com.uqcs.mobile.R
import com.uqcs.mobile.data.classes.Event
import kotlinx.android.synthetic.main.event_details_layout.*


class EventXCalendarDetailsDialog(private var activity: Activity, private var event: EventX) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.event_details_layout)

        descriptionText.movementMethod = ScrollingMovementMethod()

        cancelButton.setOnClickListener { dismiss() }
        locationText.text = event.location
        dateText.text = event.start.dateTime //todo fix formatting
        titleText.text = event.summary
        descriptionText.text = event.description
    }
}

