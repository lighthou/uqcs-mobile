package com.uqcs.mobile.features.memberslist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.common.AuthenticatedFragment

class MembersListFragmentX : Fragment(), AuthenticatedFragment {

    private lateinit var viewModel : MembersListViewModel

    companion object {
        fun newInstance(): MembersListFragmentX {
            return MembersListFragmentX()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MembersListViewModel::class.java)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun registerServerCredentials() {
        val username = (context as MainActivity).username
        val password = (context as MainActivity).password
        viewModel.registerCredentials(username, password)
    }

}