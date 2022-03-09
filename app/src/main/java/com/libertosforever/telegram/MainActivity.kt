package com.libertosforever.telegram

import AppDrawer
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.libertosforever.telegram.database.AUTH
import com.libertosforever.telegram.database.initFirebase
import com.libertosforever.telegram.database.initUser
import com.libertosforever.telegram.databinding.ActivityMainBinding
import com.libertosforever.telegram.ui.screens.main_list.MainListFragment
import com.libertosforever.telegram.ui.screens.register.EnterPhoneNumberFragment
import com.libertosforever.telegram.utilits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    lateinit var mToolBar: Toolbar
    lateinit var mAppDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch { initContacts() }
            initFields()
            initFunc()
        }
    }

    private fun initFunc() {
        setSupportActionBar(mToolBar)
        if (AUTH.currentUser != null) {
            mAppDrawer.create()
            replaceFragment(MainListFragment())
        } else {
            replaceFragment(EnterPhoneNumberFragment())
        }

    }

    private fun initFields() {
        mToolBar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            initContacts()
        }
    }

}