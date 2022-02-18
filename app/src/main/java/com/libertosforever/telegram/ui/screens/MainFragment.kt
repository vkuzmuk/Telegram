package com.libertosforever.telegram.ui.screens

import androidx.fragment.app.Fragment
import com.libertosforever.telegram.R
import com.libertosforever.telegram.utilits.APP_ACTIVITY
import com.libertosforever.telegram.utilits.hideKeyboard

class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }
}