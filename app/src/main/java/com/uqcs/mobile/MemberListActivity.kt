package com.uqcs.mobile

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONArray
import org.json.JSONObject

import android.support.v7.widget.SearchView
import com.sortabletableview.recyclerview.TableDataColumnAdapterDelegator
import com.sortabletableview.recyclerview.TableView
import com.sortabletableview.recyclerview.toolkit.*


class MemberListActivity : AppCompatActivity() {
    private val EVENTS_URL = "http://www.ryankurz.me/members"
    private var membersList = mutableListOf<Member>()
    private var requestQueue : RequestQueue? = null
    private var filterHelper: FilterHelper<Member>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)
        progress_overlay.loading_text.text = getString(R.string.fetching_members)

        val colorOddRows = ContextCompat.getColor(this, R.color.colorOddRows);
        val colorEvenRows = ContextCompat.getColor(this, R.color.colorEvenRows);
        tableView.dataRowBackgroundProvider =
                TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows);









        // ------------------------------------------------------------------
        val tableView : TableView<Member> = tableView as TableView<Member>
        filterHelper = FilterHelper<Member>(tableView)
        searchView.queryHint = "Search Here"



        val onQueryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                filterHelper?.setFilter(MemberFilter(query as String))
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        }

        val onClose = SearchView.OnCloseListener {
            filterHelper?.clearFilter()
            false
        }


        searchView.setOnQueryTextListener(onQueryTextListener)
        searchView.setOnCloseListener(onClose)
        // ------------------------------------------------------------------















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

                    membersList.add(tempMember)
                }
                val dataAdapter = TableDataColumnAdapterDelegator(this, membersList)
                dataAdapter.setColumnAdapter(0, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forFirstName()))
                dataAdapter.setColumnAdapter(1, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forLastName()))
                dataAdapter.setColumnAdapter(2, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forEmail()))
                dataAdapter.setColumnAdapter(3, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forPaid()))
                tableView.dataAdapter = dataAdapter
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(this, "Failed to fetch members list", Toast.LENGTH_LONG).show()
            })
    }
}
