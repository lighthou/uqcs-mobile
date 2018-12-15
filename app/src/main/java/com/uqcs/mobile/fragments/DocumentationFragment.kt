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
import android.support.v4.view.MenuItemCompat.getActionView
import android.support.v7.widget.SearchView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sign_in.view.*
import ru.noties.markwon.Markwon


class DocumentationFragment : ListFragment() {
    private val DOCS_URL = "http://www.ryankurz.me/docs"
    private var adapter: ArrayAdapter<String>? = null
    private var listItems : MutableList<String>? = mutableListOf()
    private lateinit var username : String
    private lateinit var password : String
    private var documentation : Documentation? = null
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            //Back button
            R.id.edit_item -> {
                markdown_view.visibility = View.GONE
                edit_view.visibility = View.VISIBLE
                documentation!!.isInEditMode = true
                updateListAndToolbar()
                true
            }
            R.id.preview -> {
                markdown_view.visibility = View.VISIBLE
                edit_view.visibility = View.GONE
                Markwon.setMarkdown(markdown_view, edit_view.text.toString())
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.documentation_toolbar_menu, menu)
        updateToolbarState(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_documentation, container, false)
    }


    private fun backPressed() {
        if (documentation!!.screenIsFile) {
            if (documentation!!.isInEditMode) {
                documentation!!.isInEditMode = false
                edit_view.visibility = View.GONE
                markdown_view.visibility = View.VISIBLE
                updateListAndToolbar()
                return
            } else {
                markdown_view.visibility = View.GONE
                list.visibility = View.VISIBLE
            }
        }
        documentation!!.goBack()
        updateListAndToolbar()
    }

    private fun onListItemSelected(selectedItem : String) {
        if (selectedItem.endsWith(".md")) {
            list.visibility = View.GONE
            markdown_view.visibility = View.VISIBLE
            val markdownText = documentation!!.fileSelected(selectedItem)
            Markwon.setMarkdown(markdown_view, markdownText)
            edit_view.setText(markdownText)
        } else {
            documentation!!.itemSelected(selectedItem)
        }
        updateListAndToolbar()
    }

    private fun updateListAndToolbar() {
        listItems?.clear()
        listItems?.addAll(documentation!!.getListState())
        adapter?.notifyDataSetChanged()
        activity?.invalidateOptionsMenu()
    }

    fun handleSearchQuery(query : String){
        listItems?.clear()
        listItems?.addAll(documentation!!.search(query))
        adapter?.notifyDataSetChanged()
    }

    private fun updateToolbarState(menu : Menu) {
        val myActionMenuItem = menu.findItem(R.id.action_search)
        myActionMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                handleSearchQuery("")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                updateListAndToolbar()
                return true
            }
        })
        val searchView = (myActionMenuItem.actionView as SearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s != "") {
                    handleSearchQuery(s)
                }
                return false
            }
        })

        if (documentation == null) return
        val sb = StringBuilder()
        for (key in documentation!!.stateKeys) {
            sb.append(key)
            sb.append('/')
        }
        documentation_toolbar.title = sb.toString()

        menu.findItem(R.id.action_search).isVisible = !documentation!!.screenIsFile
        menu.findItem(R.id.edit_item).isVisible = documentation!!.screenIsFile && !documentation!!.isInEditMode
        menu.findItem(R.id.preview).isVisible = documentation!!.screenIsFile && documentation!!.isInEditMode


        if (documentation?.stateKeys!!.isEmpty()) {
            documentation_toolbar.navigationIcon = null
        } else {
            documentation_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
            documentation_toolbar.setNavigationOnClickListener {
                backPressed()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        documentation_toolbar.title = ""

        (activity as AppCompatActivity).setSupportActionBar(documentation_toolbar)

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
                updateListAndToolbar()
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