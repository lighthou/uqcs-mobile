package com.uqcs.uqcs_mobile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun onMembersListClick(v: View?) {
        val membersListIntent = Intent(this, MemberListActivity::class.java)
        startActivity(membersListIntent)
    }

    fun onDocumentationClick(v: View?) {
        val documentationIntent = Intent(this, DocumentationActivity::class.java)
        startActivity(documentationIntent)
    }

    fun onCalenderClick(v: View?) {
        val documentationIntent = Intent(this, EventsCalendarActivity::class.java)
        startActivity(documentationIntent)
    }

}
