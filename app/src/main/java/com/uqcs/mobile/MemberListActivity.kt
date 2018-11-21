package com.uqcs.mobile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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

class MemberListActivity : AppCompatActivity() {
    private val EVENTS_URL = "http://www.ryankurz.me/members"
    private val membersList = mutableListOf<Array<String>>()
    private var requestQueue : RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)

        progress_overlay.loading_text.text = getString(R.string.fetching_events)
        Util.animateView(this, progress_overlay, View.VISIBLE, 0.8f, 200)

        requestQueue = Volley.newRequestQueue(this)
        val request = getMembersListRequest()
        request.retryPolicy = DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue?.add(getMembersListRequest())
    }


    private fun getMembersListRequest() : JsonArrayRequest {

        return JsonArrayRequest(
            Request.Method.GET, EVENTS_URL, null,
            Response.Listener<JSONArray> { response ->
                val responseString = response.toString()
                Log.i("VolleyIssues", responseString) //todo remove
                val members = JSONArray(responseString)
                for (i in 0..(members.length() - 1)) {
                    val member: JSONObject = members.getJSONObject(i)
                    val tempMember : Member = Member(member.getString("first_name"),
                                                     member.getString("last_name"),
                                                     member.getString("email"),
                                                     member.getBoolean("paid"))

                    membersList.add(arrayOf(tempMember.firstName,
                                            tempMember.lastName,
                                            tempMember.email,
                                            tempMember.paid.toString())
                    )
                }
                tableView.dataAdapter = ((SimpleTableDataAdapter(this, membersList)))
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(this, "Failed to fetch members list", Toast.LENGTH_LONG).show()
            })
    }
}
