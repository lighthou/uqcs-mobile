package com.uqcs.mobile

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.commit_dialog.*
import kotlinx.android.synthetic.main.event_details_layout.*

class CommitDialog(private var activity: Activity) : Dialog(activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.commit_dialog)

        commit_msg.requestFocus()

        commit_btn.setOnClickListener {
            if (commit_msg.text.toString() == "") {
                Toast.makeText(activity, "Commit message is required.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "Not implemented", Toast.LENGTH_LONG).show()

            }
        }

        cancel_btn.setOnClickListener {
            dismiss()
        }
    }
}