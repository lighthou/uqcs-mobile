package com.uqcs.mobile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.uqcs.mobile.fragments.CalendarFragment
import com.uqcs.mobile.fragments.MembersListFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val SELECTED_ITEM = "arg_selected_item"
    private var selectedMenuItem : Int? = null
    var username : String = ""
    var password : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.getStringExtra("USERNAME")
        password = intent.getStringExtra("PASSWORD")

        bottom_nav.setOnNavigationItemSelectedListener { menuItem ->
            selectFragment(menuItem)
            true
        }

        val selectedItem: MenuItem
        if (savedInstanceState != null) {
            selectedMenuItem = savedInstanceState.getInt(SELECTED_ITEM, 0)
            selectedItem = bottom_nav.menu.findItem(selectedMenuItem ?: return)
        } else {
            selectedItem = bottom_nav.menu.getItem(1)
        }
        selectFragment(selectedItem)
    }

    private fun selectFragment(item: MenuItem) {
        var frag: Fragment? = null
        // init corresponding fragment
        when (item.itemId) {
            R.id.menu_calendar -> frag = CalendarFragment.newInstance()
            R.id.menu_members -> frag = MembersListFragment.newInstance()
        }

        // update selected item
        selectedMenuItem = item.itemId

        // uncheck the other items.
        for (i in 0 until bottom_nav.menu.size()) {
            val menuItem = bottom_nav.menu.getItem(i)
            menuItem.isChecked = menuItem.itemId == item.itemId
        }

        if (frag != null) {
            container.removeAllViews()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.container, frag, frag.tag)
            ft.commit()
        }
    }


}
