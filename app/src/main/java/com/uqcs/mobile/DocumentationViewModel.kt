package com.uqcs.mobile


import androidx.lifecycle.ViewModel
import com.uqcs.mobile.data.classes.DocumentationState
import com.uqcs.mobile.data.classes.DocumentationState.*
import com.uqcs.mobile.data.classes.Documentation


class DocumentationViewModel : ViewModel() {

    private val DOCUMENTATION_URL = "http://www.ryankurz.me/docs"
    private lateinit var documentationStore : Documentation
    private var screenState : DocumentationState = LIST






}