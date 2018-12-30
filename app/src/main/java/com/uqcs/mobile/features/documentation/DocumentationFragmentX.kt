package com.uqcs.mobile.features.documentation

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.uqcs.mobile.common.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import kotlinx.android.synthetic.main.activity_documentation.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import ru.noties.markwon.Markwon

class DocumentationFragmentX : ListFragment(), AuthenticatedFragment {

    private lateinit var viewModel : DocumentationViewModel
    private lateinit var adapter : ArrayAdapter<String>
    private lateinit var builder : AlertDialog.Builder
    private lateinit var uneditedText : String

    private var searching = false
    private var myState : DocumentationState = DocumentationState.INITIAL
    private var fileHasBeenEdited : Boolean = false
    private var listItems = mutableListOf<String>()

    companion object {
        fun newInstance(): DocumentationFragmentX {
            return DocumentationFragmentX()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(DocumentationViewModel::class.java)
        setUpObservers()
        registerServerCredentials()

        viewModel.getDocumentationFromServer() //todo should be in init of viewmodel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_documentation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        documentation_toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(documentation_toolbar)
        progress_overlay.loading_text.text = getString(R.string.fetching_documentation)

        list.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            viewModel.onListItemSelected(selectedItem, searching)
        }

        val dialogClickListener = DialogInterface.OnClickListener { _, item ->
            when (item) {
                DialogInterface.BUTTON_POSITIVE -> {
                    fileHasBeenEdited = false
                    Markwon.setMarkdown(markdown_view, uneditedText)
                    edit_view.setText(uneditedText)
                    viewModel.onBackPressed()
                }

                DialogInterface.BUTTON_NEGATIVE -> {}
            }
        }

        builder = AlertDialog.Builder(context!!)
        builder.setTitle("Confirm Cancel")
        builder.setMessage("Discard changes?")
            .setPositiveButton("Discard", dialogClickListener)
            .setNegativeButton("Cancel", dialogClickListener)


        edit_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edit_view.text.toString() != uneditedText) {
                    fileHasBeenEdited = true
                    activity?.invalidateOptionsMenu()
                } else {
                    fileHasBeenEdited = false
                    activity?.invalidateOptionsMenu()
                }
            }
        })


        adapter = ArrayAdapter(context!!, R.layout.documentation_list_item, listItems)
        listAdapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.documentation_toolbar_menu, menu)
        updateToolbar(menu!!)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_item -> {
                viewModel.setEditMode()
                true
            }
            R.id.preview -> {
                Markwon.setMarkdown(markdown_view, edit_view.text.toString())
                viewModel.setPreviewMode()
                true
            }
            R.id.save -> {
                val dialog = DocumentationCommitDialog(activity!!)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    override fun registerServerCredentials() {
        val username = (context as MainActivity).username
        val password = (context as MainActivity).password
        viewModel.registerCredentials(username, password)
    }

    private fun setToolbarIconVisibility(menu : Menu) {
        //Search Icon
        menu.findItem(R.id.action_search).isVisible = myState == DocumentationState.LIST ||
                myState == DocumentationState.INITIAL
        //Edit File Icon
        menu.findItem(R.id.edit_item).isVisible = myState == DocumentationState.VIEW_FILE ||
                myState == DocumentationState.PREVIEW_FILE
        //Preview Icon
        menu.findItem(R.id.preview).isVisible = myState == DocumentationState.EDIT_FILE
        //Save Icon
        menu.findItem(R.id.save).isVisible = fileHasBeenEdited
    }

    private fun updateToolbar(menu : Menu) {
        setToolbarIconVisibility(menu)
        setSearchTextChangedListener(menu)
        if (myState == DocumentationState.INITIAL) {
            documentation_toolbar.navigationIcon = null
        } else {
            documentation_toolbar.setNavigationIcon(if (fileHasBeenEdited) R.drawable.ic_clear else
                                                                           R.drawable.ic_arrow_back)
            documentation_toolbar.setNavigationOnClickListener {
                if (fileHasBeenEdited) builder.show() else viewModel.onBackPressed()
            }
        }
    }

    private fun setSearchTextChangedListener(menu : Menu) {
        val myActionMenuItem = menu.findItem(R.id.action_search)
        myActionMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.search("")
                searching = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searching = false
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
                    if (s != "") viewModel.search(s)
                    return false
                }
            })
        }
    }

    private fun updateViewByScreenState(state : DocumentationState) {
        when (state) {
            DocumentationState.INITIAL -> {
                markdown_view.visibility = View.GONE
                list.visibility = View.VISIBLE
                edit_view.visibility = View.GONE
            }
            DocumentationState.LIST -> {
                markdown_view.visibility = View.GONE
                list.visibility = View.VISIBLE
                edit_view.visibility = View.GONE
            }
            DocumentationState.VIEW_FILE -> {
                Util.closeKeyboardIfPresent(context!!, activity?.currentFocus)
                markdown_view.visibility = View.VISIBLE
                list.visibility = View.GONE
                edit_view.visibility = View.GONE
            }
            DocumentationState.EDIT_FILE -> {
                markdown_view.visibility = View.GONE
                list.visibility = View.GONE
                edit_view.visibility = View.VISIBLE
            }
            DocumentationState.PREVIEW_FILE -> {
                markdown_view.visibility = View.VISIBLE
                list.visibility = View.GONE
                edit_view.visibility = View.GONE
            }
        }
    }

    private fun setUpObservers() {
        viewModel.showLoading.observe(this, Observer<Boolean> { shouldShowLoading ->
            if (shouldShowLoading) showLoadingOverlay() else hideLoadingOverlay()
        })

        viewModel.listItems.observe(this, Observer<List<String>> { viewModelItems ->
            listItems.clear()
            listItems.addAll(viewModelItems)
            adapter.notifyDataSetChanged()
        })
        
        viewModel.screenState.observe(this, Observer<DocumentationState> { state ->
            myState = state
            updateViewByScreenState(state)
            activity?.invalidateOptionsMenu()
        })

        viewModel.textData.observe(this, Observer<String> { text ->
            uneditedText = text
            Markwon.setMarkdown(markdown_view, text)
            edit_view.setText(text)
        })

        viewModel.titleText.observe(this, Observer<String> { text ->
            documentation_toolbar.title = text
        })
    }

    private fun showLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
    }

    private fun hideLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
    }
}