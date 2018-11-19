package com.uqcs.uqcs_mobile

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.event_details_layout.*
import android.app.Activity
import android.text.method.ScrollingMovementMethod


class EventDetailsDialog(var activity: Activity, var event: Event) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.event_details_layout)

        descriptionText.movementMethod = ScrollingMovementMethod();

        cancelButton.setOnClickListener { dismiss() }
        locationText.text = event.location
        dateText.text = event.date.toString()
        titleText.text = event.summary
        descriptionText.text = event.description
    }
}

