package com.uqcs.mobile.features.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment

class AnnouncementFragment : Fragment(), AuthenticatedFragment {


    companion object {
        fun newInstance(): AnnouncementFragment {
            return AnnouncementFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_announcements, container, false)
    }

    override fun registerServerCredentials() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}