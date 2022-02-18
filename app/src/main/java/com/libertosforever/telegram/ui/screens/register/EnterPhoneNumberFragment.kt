package com.libertosforever.telegram.ui.screens.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.AUTH
import com.libertosforever.telegram.databinding.FragmentEnterPhoneNumberBinding
import com.libertosforever.telegram.utilits.*
import java.util.concurrent.TimeUnit

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {
    private lateinit var mBinding: FragmentEnterPhoneNumberBinding
    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEnterPhoneNumberBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        AUTH = FirebaseAuth.getInstance()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Welcome")
                        restartActivity()
                    } else showToast(task.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                showToast(e.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))
            }
        }
        mBinding.registerBtnNext.setOnClickListener {
            sendCode()
        }
    }

    private fun sendCode() {
        if (mBinding.registerInputPhoneNumber.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = mBinding.registerInputPhoneNumber.text.toString()

        val options = PhoneAuthOptions
            .newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(mPhoneNumber)
            .setActivity(APP_ACTIVITY)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(mCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}