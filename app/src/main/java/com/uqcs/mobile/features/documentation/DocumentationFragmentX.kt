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
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.uqcs.mobile.CommitDialog
import com.uqcs.mobile.Helpers.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import com.uqcs.mobile.data.classes.DocumentationState
import kotlinx.android.synthetic.main.activity_documentation.*
import kotlinx.android.synthetic.main.loading_overlay.*
import ru.noties.markwon.Markwon

class DocumentationFragmentX : ListFragment(), AuthenticatedFragment {

    private lateinit var viewModel : DocumentationViewModel
    private lateinit var adapter : ArrayAdapter<String>
    private lateinit var builder : AlertDialog.Builder
    private lateinit var uneditedText : String

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
        viewModel.getDocumentationFromServer()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_documentation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        documentation_toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(documentation_toolbar)


        list.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            viewModel.onListItemSelected(selectedItem)
        }

        val dialogClickListener = DialogInterface.OnClickListener { dialog, item ->
            when (item) {
                DialogInterface.BUTTON_POSITIVE -> {
                    fileHasBeenEdited = false
                    Markwon.setMarkdown(markdown_view, uneditedText)
                    edit_view.setText(uneditedText)
                    viewModel.onBackPressed()
                    //updateListAndToolbar()
                }

                DialogInterface.BUTTON_NEGATIVE -> {}
            }
        }

        builder = AlertDialog.Builder(context!!)
        builder.setTitle("Confirm Cancel")
        builder.setMessage("Clear all changes?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)


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
                val dialog = CommitDialog(activity!!)
                dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        menu.findItem(R.id.action_search).isVisible = myState == DocumentationState.LIST
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
        if (myState == DocumentationState.INITIAL) {
            documentation_toolbar.navigationIcon = null
        } else {
            documentation_toolbar.setNavigationIcon(if (fileHasBeenEdited) R.drawable.abc_ic_clear_material else
                                                                           R.drawable.abc_ic_ab_back_material)
            documentation_toolbar.setNavigationOnClickListener {
                if (fileHasBeenEdited) builder.show() else viewModel.onBackPressed()
            }
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