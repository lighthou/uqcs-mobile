package com.uqcs.uqcs_mobile

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.activity_event_details.*
import android.R.attr.delay
import android.app.Activity
import android.content.Context


class EventDetailsDialog(var activity: Activity, var event: Event) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_event_details)

        cancelButton.setOnClickListener { dismiss() }
        locationText.text = event.location
        dateText.text = event.date.toString()
        titleText.text = event.summary
        descriptionText.text = event.description
    }
}

