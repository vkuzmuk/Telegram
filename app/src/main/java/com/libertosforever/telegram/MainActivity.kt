package com.libertosforever.telegram

import AppDrawer
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.libertosforever.telegram.activities.RegisterActivity
import com.libertosforever.telegram.databinding.ActivityMainBinding
import com.libertosforever.telegram.models.User
import com.libertosforever.telegram.ui.fragments.ChatsFragment
import com.libertosforever.telegram.utilits.*
import com.theartofdev.edmodo.cropper.CropImage

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mToolBar: Toolbar
    lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            initFields()
            initFunc()
        }

    }

    private fun initFunc() {
        if (AUTH.currentUser != null) {
            setSupportActionBar(mToolBar)
            mAppDrawer.create()
            replaceFragment(ChatsFragment())
        } else {
            replaceActivity(RegisterActivity())
        }

    }

    private fun initFields() {
        mToolBar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolBar)
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)

    }

}