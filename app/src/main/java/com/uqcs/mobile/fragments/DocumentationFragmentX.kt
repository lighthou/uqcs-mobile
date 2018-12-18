package com.uqcs.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import com.uqcs.mobile.DocumentationViewModel

class DocumentationFragmentX : ListFragment() {

    private lateinit var viewModel : DocumentationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(DocumentationViewModel::class.java)
        viewModel.registerCredentials("username", "password")
    }
}