package com.uqcs.mobile

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONArray
import org.json.JSONObject
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import android.support.v4.view.MenuItemCompat.getActionView
import android.R.menu
import android.view.Menu
import android.view.MenuInflater









class MemberListActivity : AppCompatActivity() {
    private val EVENTS_URL = "http://www.ryankurz.me/members"
    private var membersList = mutableListOf<Array<String>>()
    private var requestQueue : RequestQueue? = null
    // todo private val filterHelper: FilterHelper<Member>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)
        progress_overlay.loading_text.text = getString(R.string.fetching_members)


        val headerAdapter =
            SimpleTableHeaderAdapter(this, "First Name", "Last Name", "Email", "Paid")
        headerAdapter.setTextColor(Color.WHITE)
        tableView.headerAdapter = headerAdapter

        requestQueue = Volley.newRequestQueue(this)
        tableView.isSwipeToRefreshEnabled = true
        tableView.setSwipeToRefreshListener { refreshMembersList() }

        tableView.setSwipeToRefreshListener { refreshIndicator ->
            refreshMembersList()
            refreshIndicator.hide()
        }

        refreshMembersList()
    }

    private fun refreshMembersList() {
        membersList.clear()
        Util.animateView(this, progress_overlay, View.VISIBLE, 0.8f, 200)
        val request = getMembersListRequest()
        request.retryPolicy = DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue?.add(getMembersListRequest())
    }


    private fun getMembersListRequest() : JsonArrayRequest {

        return JsonArrayRequest(
            Request.Method.GET, EVENTS_URL, null,
            Response.Listener<JSONArray> { response ->
                val responseString = response.toString()
                val members = JSONArray(responseString)
                for (i in 0..(members.length() - 1)) {
                    val member: JSONObject = members.getJSONObject(i)
                    val tempMember = Member(member.getString("first_name"),
                                                     member.getString("last_name"),
                                                     member.getString("email"),
                                                     member.getBoolean("paid"))

                    membersList.add(arrayOf(tempMember.firstName,
                                            tempMember.lastName,
                                            tempMember.email,
                                            tempMember.paid.toString())
                    )
                }
                tableView.dataAdapter = (SimpleTableDataAdapter(this, membersList))
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(this, "Failed to fetch members list", Toast.LENGTH_LONG).show()
            })
    }

/*
    fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_search_datamain, menu)
        val item = menu.findItem(R.id.menu_search)
        val searchView = item.getActionView() as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener() {
            fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            fun onQueryTextChange(query: String): Boolean {
                // ******************** Interesting Code Section **********************************************************
                filterHelper.setFilter(FlightFilter(query))
                // ******************** Interesting Code Section **********************************************************
                return false
            }
        })
        searchView.setOnCloseListener(object : SearchView.OnCloseListener() {
            fun onClose(): Boolean {
                // ******************** Interesting Code Section **********************************************************
                filterHelper.clearFilter()
                // ******************** Interesting Code Section **********************************************************
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }*/
}
