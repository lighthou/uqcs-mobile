package com.uqcs.mobile.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uqcs.mobile.Helpers.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.data.classes.Member
import kotlinx.android.synthetic.main.activity_documentation.*
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONArray
import org.json.JSONObject
import android.widget.ArrayAdapter
import com.uqcs.mobile.data.classes.Documentation


class DocumentationFragment : ListFragment() {
    private var requestQueue: RequestQueue? = null
    private val DOCS_URL = "http://www.ryankurz.me/docs"
    private var username = ""
    private var password = ""
    private var adapter: ArrayAdapter<String>? = null
    private var listItems : MutableList<String>? = mutableListOf()
    private var documentation : Documentation? = null
    companion object {
        fun newInstance(): DocumentationFragment {
            return DocumentationFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_documentation, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_overlay.loading_text.text = getString(R.string.fetching_members)

        username = (context as MainActivity).username
        password = (context as MainActivity).password

        requestQueue = Volley.newRequestQueue(context!!)
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)

        requestQueue?.add(getDocsRequest())

        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, listItems as MutableList)
        listAdapter = adapter
    }

    private fun getDocsRequest() : JsonObjectRequest {

        return object : JsonObjectRequest(
            Request.Method.GET, DOCS_URL, null,
            Response.Listener<JSONObject> { response ->
                val responseString = response.toString()
                val docs = JSONObject(responseString)
                documentation = Documentation(docs)
                listItems?.clear()
                listItems?.addAll(documentation!!.getListState())
                adapter?.notifyDataSetChanged()

                list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position) as String
                    if (selectedItem.endsWith(".md")) {
                        documentation!!.fileSelected(selectedItem)
                    }
                    documentation!!.itemSelected(selectedItem)
                    listItems?.clear()
                    listItems?.addAll(documentation!!.getListState())
                    adapter?.notifyDataSetChanged()
                }

                Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(context!!, "Failed to fetch docs", Toast.LENGTH_LONG).show()
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