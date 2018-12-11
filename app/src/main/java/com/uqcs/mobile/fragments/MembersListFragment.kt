package com.uqcs.mobile.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.sortabletableview.recyclerview.SortableTableView
import com.sortabletableview.recyclerview.TableDataColumnAdapterDelegator
import com.sortabletableview.recyclerview.model.TableColumnWeightModel
import com.sortabletableview.recyclerview.toolkit.*
import com.uqcs.mobile.R
import com.uqcs.mobile.Helpers.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.data.classes.Member
import com.uqcs.mobile.tableview.MemberComparator
import com.uqcs.mobile.tableview.MemberFilter
import com.uqcs.mobile.tableview.MemberStringValueExtractor
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MembersListFragment : Fragment() {

    private var filterHelper: FilterHelper<Member>? = null
    private val EVENTS_URL = "http://www.ryankurz.me/members"
    private val membersList : ArrayList<Member> = ArrayList<Member>()
    private var requestQueue : RequestQueue? = null
    private var username = ""
    private var password = ""

    companion object {
        fun newInstance(): MembersListFragment {
            return MembersListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_member_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_overlay.loading_text.text = getString(R.string.fetching_members)
        searchView.queryHint = "Search Here"
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
        username = (context as MainActivity).username
        password = (context as MainActivity).password
        requestQueue = Volley.newRequestQueue(context!!)

        tableView.isSwipeToRefreshEnabled = true

        tableView.setSwipeToRefreshListener { refreshIndicator ->
            refreshMembersList()
            refreshIndicator.hide()
        }
        refreshMembersList()
    }

    private fun refreshMembersList() {
        membersList.clear()
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
        val request = getMembersListRequest()
        request.retryPolicy = DefaultRetryPolicy( 30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue?.add(getMembersListRequest())
    }


    private fun setUpTable() {
        // set up header adapter
        val headerAdapter = SimpleTableHeaderAdapter(context!!, "First", "Last", "Email", "Paid")
        // set up data adapter
        val dataAdapter = TableDataColumnAdapterDelegator<Member>(context!!, membersList)
        dataAdapter.apply {
            setColumnAdapter(0, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forFirstName()))
            setColumnAdapter(1, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forLastName()))
            setColumnAdapter(2, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forEmail()))
            setColumnAdapter(3, SimpleTableDataColumnAdapter(MemberStringValueExtractor.forPaid()))
        }


        // set up the table view
        val tableView = view?.findViewById<SortableTableView<Member>>(R.id.tableView)
        tableView?.headerAdapter = headerAdapter
        tableView?.dataAdapter = dataAdapter

        // do some styling
        val colorOddRows = ContextCompat.getColor(context!!, R.color.colorOddRows)
        val colorEvenRows = ContextCompat.getColor(context!!, R.color.colorEvenRows)
        tableView?.dataRowBackgroundProvider =
                TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows)

        // change column widths
        val tableColumnModel = TableColumnWeightModel(4)
        tableColumnModel.apply {
            setColumnWeight(0, 4)
            setColumnWeight(1, 4)
            setColumnWeight(2, 4)
            setColumnWeight(3, 4)
        }

        tableView?.columnModel = tableColumnModel

        tableView?.headerSortStateViewProvider = SortStateViewProviders.brightArrows();

        tableView?.apply {
            setColumnComparator(0, MemberComparator.forFirstName())
            setColumnComparator(1, MemberComparator.forLastName())
            setColumnComparator(2, MemberComparator.forEmail())
            setColumnComparator(3, MemberComparator.forPaid())
        }


        filterHelper = FilterHelper(tableView)

        val searchView = view?.findViewById<SearchView>(R.id.searchView)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                filterHelper!!.setFilter(MemberFilter(query))
                return false
            }
        })
        searchView?.setOnCloseListener {
            filterHelper!!.clearFilter()
            false
        }
    }

    private fun getMembersListRequest() : JsonArrayRequest {

        return object : JsonArrayRequest(
            Request.Method.GET, EVENTS_URL, null,
            Response.Listener<JSONArray> { response ->
                val responseString = response.toString()
                val members = JSONArray(responseString)
                membersList.clear()
                for (i in 0..(members.length() - 1)) {
                    val member: JSONObject = members.getJSONObject(i)
                    val tempMember = Member(
                        member.getString("first_name"),
                        member.getString("last_name"),
                        member.getString("email"),
                        member.getBoolean("paid")
                    )

                    membersList.add(tempMember)
                }
                setUpTable()
                Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(context!!, "Failed to fetch members list", Toast.LENGTH_LONG).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = mutableMapOf<String, String>()
                val userAndPassword = "$username:$password"
                val basicAuth = "Basic " + Base64.encodeToString(userAndPassword.toByteArray(), Base64.NO_WRAP)
                params["Authorization"] = basicAuth
                return params
            }
        }
    }
}