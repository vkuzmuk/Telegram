package com.libertosforever.telegram.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.libertosforever.telegram.R
import com.libertosforever.telegram.databinding.ActivityRegisterBinding
import com.libertosforever.telegram.ui.fragments.EnterPhoneNumberFragment
import com.libertosforever.telegram.utilits.AUTH
import com.libertosforever.telegram.utilits.initFirebase
import com.libertosforever.telegram.utilits.replaceFragment

class RegisterActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolBar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        mToolBar = mBinding.registerToolbar
        setSupportActionBar(mToolBar)
        title = getString(R.string.register_title_your_phone)
        replaceFragment(EnterPhoneNumberFragment())
    }

}