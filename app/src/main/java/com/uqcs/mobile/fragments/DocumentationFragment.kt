package com.uqcs.mobile.fragments

import android.os.Bundle
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
import com.uqcs.mobile.common.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import org.json.JSONObject
import android.widget.ArrayAdapter
import com.uqcs.mobile.data.classes.Documentation
import android.text.Editable
import android.text.TextWatcher
import com.uqcs.mobile.features.documentation.DocumentationState
import ru.noties.markwon.Markwon
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.ListFragment
import com.uqcs.mobile.features.documentation.DocumentationCommitDialog
import com.uqcs.mobile.features.documentation.DocumentationViewModel
import kotlinx.android.synthetic.main.activity_documentation.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*


class DocumentationFragment : ListFragment() {

    private lateinit var viewModel : DocumentationViewModel

    private val DOCS_URL = "http://www.ryankurz.me/docs"
    private var adapter: ArrayAdapter<String>? = null
    private var listItems : MutableList<String>? = mutableListOf()
    private lateinit var username : String
    private lateinit var password : String
    private var documentation : Documentation? = null
    private lateinit var requestQueue: RequestQueue
    private var uneditedText = ""
    private lateinit var builder : AlertDialog.Builder

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
        return when (item.itemId) {
            R.id.edit_item -> {
                markdown_view.visibility = View.GONE
                edit_view.visibility = View.VISIBLE
                documentation!!.screenState = DocumentationState.EDIT_FILE
                updateListAndToolbar()
                true
            }
            R.id.preview -> {
                markdown_view.visibility = View.VISIBLE
                edit_view.visibility = View.GONE
                Markwon.setMarkdown(markdown_view, edit_view.text.toString())
                documentation!!.screenState = DocumentationState.PREVIEW_FILE
                updateListAndToolbar()
                true
            }
            R.id.save -> {
                val dialog = DocumentationCommitDialog(activity!!)
                dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                dialog.show()
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
        when (documentation!!.screenState) {
            DocumentationState.LIST -> {
                documentation!!.goBack()
                updateListAndToolbar()
            }
            DocumentationState.VIEW_FILE -> {
                documentation!!.screenState = DocumentationState.LIST
                markdown_view.visibility = View.GONE
                list.visibility = View.VISIBLE
                documentation!!.goBack()
                updateListAndToolbar()
            }
            DocumentationState.EDIT_FILE -> {
                if (documentation!!.fileHasBeenEdited) {
                    builder.show()
                } else {
                    documentation!!.screenState = DocumentationState.VIEW_FILE
                    edit_view.visibility = View.GONE
                    markdown_view.visibility = View.VISIBLE
                    Markwon.setMarkdown(markdown_view, uneditedText)
                    edit_view.setText(uneditedText)
                    updateListAndToolbar()
                }

            }
            DocumentationState.PREVIEW_FILE -> {
                if (documentation!!.fileHasBeenEdited) {
                    builder.show()
                } else {
                    documentation!!.screenState = DocumentationState.VIEW_FILE
                    edit_view.visibility = View.GONE
                    markdown_view.visibility = View.VISIBLE
                    Markwon.setMarkdown(markdown_view, uneditedText)
                    edit_view.setText(uneditedText)
                    updateListAndToolbar()
                }
            }
        }
    }

    private fun onListItemSelected(selectedItem : String) {
        if (selectedItem.endsWith(".md")) {

            list.visibility = View.GONE
            markdown_view.visibility = View.VISIBLE
            uneditedText = documentation!!.fileSelected(selectedItem)
            Markwon.setMarkdown(markdown_view, uneditedText)
            edit_view.setText(uneditedText)
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

        if (myActionMenuItem.actionView != null) {
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
        }


        if (documentation == null) return
        if (documentation!!.fileHasBeenEdited) {
            documentation_toolbar.title = "Editing..."
        } else {
            val sb = StringBuilder()
            for (key in documentation!!.stateKeys) {
                sb.append(key)
                sb.append('/')
            }
            documentation_toolbar.title = sb.toString()
        }
        setToolbarIconVisibility(menu)


        if (documentation!!.stateKeys.isEmpty()) {
            documentation_toolbar.navigationIcon = null
        } else {
            if (documentation!!.fileHasBeenEdited) {
                documentation_toolbar.setNavigationIcon(R.drawable.abc_ic_clear_material)
            } else {
                documentation_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
            }
            documentation_toolbar.setNavigationOnClickListener {
                backPressed()
            }
        }
    }

    private fun setToolbarIconVisibility(menu : Menu) {
        //Search Icon
        menu.findItem(R.id.action_search).isVisible = documentation!!.screenState == DocumentationState.LIST
        //Edit File Icon
        menu.findItem(R.id.edit_item).isVisible = documentation!!.screenState == DocumentationState.VIEW_FILE ||
                documentation!!.screenState == DocumentationState.PREVIEW_FILE
        //Preview Icon
        menu.findItem(R.id.preview).isVisible = documentation!!.screenState == DocumentationState.EDIT_FILE
        //Save Icon
        menu.findItem(R.id.save).isVisible = documentation!!.fileHasBeenEdited
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        documentation_toolbar.title = ""

        (activity as AppCompatActivity).setSupportActionBar(documentation_toolbar)

        edit_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edit_view.text.toString() != uneditedText) {
                    documentation!!.fileHasBeenEdited = true
                    activity?.invalidateOptionsMenu()
                } else {
                    documentation!!.fileHasBeenEdited = false
                    activity?.invalidateOptionsMenu()
                }
            }
        })

        val dialogClickListener = DialogInterface.OnClickListener { dialog, item ->
            when (item) {
                DialogInterface.BUTTON_POSITIVE -> {
                    documentation!!.screenState = DocumentationState.VIEW_FILE
                    edit_view.visibility = View.GONE
                    markdown_view.visibility = View.VISIBLE
                    Markwon.setMarkdown(markdown_view, uneditedText)
                    edit_view.setText(uneditedText)
                    updateListAndToolbar()
                }
                DialogInterface.BUTTON_NEGATIVE -> {}
            }
        }

        builder = AlertDialog.Builder(context!!)
        builder.setTitle("Confirm Cancel")
        builder.setMessage("Clear all changes?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)

        progress_overlay.loading_text.text = getString(R.string.fetching_documentation)

        username = (context as MainActivity).username
        password = (context as MainActivity).password

        requestQueue = Volley.newRequestQueue(context!!)
        showLoadingScreen()

        requestQueue.add(getDocsRequest())
        android.R.layout.simple_list_item_1


        adapter = ArrayAdapter(context!!, R.layout.documentation_list_item, listItems as MutableList)
        listAdapter = adapter
    }


    private fun getDocsRequest() : JsonObjectRequest {

        return object : JsonObjectRequest(
            Request.Method.GET, DOCS_URL, null,
            Response.Listener<JSONObject> { response ->
                val docs = JSONObject(response.toString())
                documentation = Documentation(docs)
                updateListAndToolbar()
                list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position) as String
                    onListItemSelected(selectedItem)
                }

                hideLoadingScreen()
            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                hideLoadingScreen()
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

    private fun showLoadingScreen() {
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
    }

    private fun hideLoadingScreen() {
        Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
    }
}