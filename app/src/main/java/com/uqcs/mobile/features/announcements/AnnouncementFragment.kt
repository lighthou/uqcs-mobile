package com.uqcs.mobile.features.announcements

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import kotlinx.android.synthetic.main.fragment_announcements.*
import ru.noties.markwon.Markwon

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

        body_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var sb = StringBuilder()
                val string = s.toString()
                for (c : Int in 0 until string.length) {
                    if (string[c] == '\n') {
                        sb.append("    \n")
                    } else {
                        sb.append(string[c])
                    }
                }
                Markwon.setMarkdown(body_preview,  sb.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_announcements, container, false)
    }

    override fun registerServerCredentials() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}