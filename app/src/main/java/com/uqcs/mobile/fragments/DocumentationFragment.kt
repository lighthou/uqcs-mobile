package com.uqcs.mobile.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.android.volley.toolbox.Volley
import com.uqcs.mobile.Helpers.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import kotlinx.android.synthetic.main.activity_documentation.*
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*


class DocumentationFragment : Fragment() {


    companion object {
        fun newInstance(): DocumentationFragment {
            return DocumentationFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_documentation, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_overlay.loading_text.text = getString(R.string.fetching_members)

        edit_ll.setOnClickListener {
            markdown_view.text = "Hello Hello"
            markdown_view.focusable = View.FOCUSABLE
            markdown_view.editableText
        }

        markdown_view.markdown = "# Accessing full members list\n" +
                "\n" +
                "**Note:** If you do not require the full table including student numbers and simply need a basic members list you may visit [the web portal](https://join.uqcs.org.au/admin/list). Username and password must be provided by the committee member who setup your members list database for the year, or should be accessable on lastpass. If you are the new committee and havent yet set up your coming years database and dont want to alter the web portal yet, ask a previous committee member for their details. If you want to make your own database for the coming year, see [here](https://github.com/UQComputingSociety/committee/blob/master/docs/processes/member-list-for-new-year.md).\n" +
                "\n" +
                "**For full access to the databases and tables in UQ cloud read below.**\n" +
                "\n" +
                "## Requirements\n" +
                "- UQ Cloud Access (walkthrough for UQ Cloud induction may be found [here](https://github.com/UQComputingSociety/committee/blob/master/docs/induction/6-uqcloud.md))\n" +
                "\n" +
                "\n" +
                "## Steps \n" +
                "1. SSH into UQ Cloud (ssh sXXXXXXX@uqcs1.uqcloud.net)\n" +
                "     - Enter university password for authentication\n" +
                "2. Get to psql with `sudo -u postgres psql`\n" +
                "     - Enter university password for authentication\n" +
                "     - You should be able to use `\\l` in order to list databases. \n" +
                "     - Here you should see, amoung others, `signup, signup2018, signup2017` etc\n" +
                "3. Access the signup table with `\\c signup` or `\\c table_name` for whichever table you'd like to see\n" +
                "     - You should see `You are now connected to database \"signup\" as user \"postgres\".` pop up in the terminal following your command \n" +
                "4. Execute sql query (examples below)\n" +
                "     - Get full list of names and if they've paid and more  `select * from member;`\n" +
                "     - Get full list of student numbers `select student_no from student;`\n" +
                "     - Get full list of student numbers and details `select * from student`\n" +
                "     - **Note: existing tables are `adminuser`, `member`, `session` and `student`**\n"
        //Util.animateView(context as Context, progress_overlay, View.VISIBLE, 0.8f, 200)

    }
}