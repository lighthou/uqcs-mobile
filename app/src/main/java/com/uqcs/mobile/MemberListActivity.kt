package com.uqcs.mobile

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.sortabletableview.recyclerview.SortableTableView
import com.sortabletableview.recyclerview.TableDataColumnAdapterDelegator
import com.sortabletableview.recyclerview.TableView
import com.sortabletableview.recyclerview.model.TableColumnWeightModel
import com.sortabletableview.recyclerview.providers.SortStateViewProvider
import com.sortabletableview.recyclerview.toolkit.*
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class MemberListActivity : AppCompatActivity() {

    private var filterHelper: FilterHelper<Member>? = null
    private val EVENTS_URL = "http://www.ryankurz.me/members"
    private val membersList : ArrayList<Member> = ArrayList<Member>()
    private var requestQueue : RequestQueue? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)
        progress_overlay.loading_text.text = getString(R.string.fetching_members)
        searchView.queryHint = "Search Here"
        Util.animateView(this, progress_overlay, View.VISIBLE, 0.8f, 200)

        requestQueue = Volley.newRequestQueue(this)

        tableView.isSwipeToRefreshEnabled = true

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


    private fun setUpTable() {
        // set up header adapter
        val headerAdapter = SimpleTableHeaderAdapter(this, "First", "Last", "Email", "Paid")
        // set up data adapter
        val dataAdapter = TableDataColumnAdapterDelegator<Member>(this, membersList)
        dataAdapter.setColumnAdapter(0, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forFirstName()))
        dataAdapter.setColumnAdapter(1, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forLastName()))
        dataAdapter.setColumnAdapter(2, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forEmail()))
        dataAdapter.setColumnAdapter(3, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forPaid()))

        // set up the table view
        val tableView = findViewById<SortableTableView<Member>>(R.id.tableView)
        tableView.headerAdapter = headerAdapter
        tableView.dataAdapter = dataAdapter

        // do some styling
        val colorOddRows = ContextCompat.getColor(this, R.color.colorOddRows)
        val colorEvenRows = ContextCompat.getColor(this, R.color.colorEvenRows)
        tableView.dataRowBackgroundProvider =
                TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows)

        // change column widths
        val tableColumnModel = TableColumnWeightModel(4)
        tableColumnModel.setColumnWeight(0, 4)
        tableColumnModel.setColumnWeight(1, 4)
        tableColumnModel.setColumnWeight(2, 4)
        tableColumnModel.setColumnWeight(3, 4)
        tableView.columnModel = tableColumnModel

        tableView.headerSortStateViewProvider = SortStateViewProviders.brightArrows();

        tableView.setColumnComparator(0, MemberComparator.forFirstName());
        tableView.setColumnComparator(1, MemberComparator.forLastName());
        tableView.setColumnComparator(2, MemberComparator.forEmail());
        tableView.setColumnComparator(3, MemberComparator.forPaid());

        filterHelper = FilterHelper(tableView)

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                filterHelper!!.setFilter(MemberFilter(query))
                return false
            }
        })
        searchView.setOnCloseListener {
            filterHelper!!.clearFilter()
            false
        }
    }

    private fun getMembersListRequest() : JsonArrayRequest {

        return JsonArrayRequest(
            Request.Method.GET, EVENTS_URL, null,
            Response.Listener<JSONArray> { response ->
                val responseString = response.toString()
                val members = JSONArray(responseString)
                membersList.clear()
                for (i in 0..(members.length() - 1)) {
                    val member: JSONObject = members.getJSONObject(i)
                    val tempMember = Member(member.getString("first_name"),
                        member.getString("last_name"),
                        member.getString("email"),
                        member.getBoolean("paid"))

                    membersList.add(tempMember)
                }
                setUpTable()
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(this, "Failed to fetch members list", Toast.LENGTH_LONG).show()
            })
    }
}