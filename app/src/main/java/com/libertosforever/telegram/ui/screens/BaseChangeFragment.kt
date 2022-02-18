package com.libertosforever.telegram.ui.screens

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.libertosforever.telegram.MainActivity
import com.libertosforever.telegram.R
import com.libertosforever.telegram.utilits.APP_ACTIVITY
import com.libertosforever.telegram.utilits.hideKeyboard

open class BaseChangeFragment(layout: Int): Fragment(layout) {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        APP_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_confirm_change -> change()
        }
        return true
    }

    open fun change() {

    }

}