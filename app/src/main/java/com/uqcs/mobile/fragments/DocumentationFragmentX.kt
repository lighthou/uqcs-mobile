package com.uqcs.mobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import com.uqcs.mobile.DocumentationViewModel
import com.uqcs.mobile.Helpers.Util
import com.uqcs.mobile.R
import kotlinx.android.synthetic.main.loading_overlay.*

class DocumentationFragmentX : ListFragment() {

    private lateinit var viewModel : DocumentationViewModel

    companion object {
        fun newInstance(): DocumentationFragmentX {
            return DocumentationFragmentX()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(DocumentationViewModel::class.java)

        viewModel.showLoading.observe(this, Observer<Boolean> { shouldShowLoading ->
            if (shouldShowLoading) showLoadingOverlay() else hideLoadingOverlay()
        })

        viewModel.registerCredentials("username", "password")
        viewModel.getDocumentationFromServer()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_documentation, container, false)
    }

    private fun showLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
    }

    private fun hideLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
    }
}