package com.uqcs.mobile.fragments

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uqcs.mobile.Helpers.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import kotlinx.android.synthetic.main.activity_documentation.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import org.json.JSONObject
import android.widget.ArrayAdapter
import com.uqcs.mobile.data.classes.Documentation


class DocumentationFragment : ListFragment() {
    private val DOCS_URL = "http://www.ryankurz.me/docs"
    private var adapter: ArrayAdapter<String>? = null
    private var listItems : MutableList<String>? = mutableListOf()
    private lateinit var username : String
    private lateinit var password : String
    private lateinit var documentation : Documentation
    private lateinit var requestQueue: RequestQueue

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
        menu.clear()
        inflater.inflate(R.menu.documentation_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_documentation, container, false)
    }


    private fun backPressed() {
        Toast.makeText(activity, "Hello", Toast.LENGTH_LONG).show()
        if (documentation.screenIsFile) {
            markdown_view.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
        documentation.goBack()
        updateList()
        setBackButtonState()
    }

    private fun onListItemSelected(selectedItem : String) {
        if (selectedItem.endsWith(".md")) {
            list.visibility = View.GONE
            markdown_view.visibility = View.VISIBLE
            markdown_view.markdown = documentation.fileSelected(selectedItem)
        } else {
            documentation.itemSelected(selectedItem)
        }
        updateList()
        setBackButtonState()
    }

    private fun updateList() {
        listItems?.clear()
        listItems?.addAll(documentation.getListState())
        adapter?.notifyDataSetChanged()
    }

    private fun setBackButtonState() {
        if (documentation.stateKeys.isEmpty()) {
            my_toolbar.navigationIcon = null
        } else {
            my_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
            my_toolbar.setNavigationOnClickListener {
                backPressed()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        my_toolbar.title = ""

        (activity as AppCompatActivity).setSupportActionBar(my_toolbar)

        progress_overlay.loading_text.text = getString(R.string.fetching_docs)

        username = (context as MainActivity).username
        password = (context as MainActivity).password

        requestQueue = Volley.newRequestQueue(context!!)
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)

        requestQueue.add(getDocsRequest())
        android.R.layout.simple_list_item_1


        adapter = ArrayAdapter(context!!, R.layout.documentation_list_item, listItems as MutableList)
        listAdapter = adapter
    }

    private fun getDocsRequest() : JsonObjectRequest {

        return object : JsonObjectRequest(
            Request.Method.GET, DOCS_URL, null,
            Response.Listener<JSONObject> { response ->
                val responseString = response.toString()
                val docs = JSONObject(responseString)
                documentation = Documentation(docs)
                updateList()
                list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position) as String
                    onListItemSelected(selectedItem)
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