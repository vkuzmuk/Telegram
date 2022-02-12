package com.libertosforever.telegram.ui.fragments.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.*
import com.libertosforever.telegram.databinding.FragmentEnterCodeBinding
import com.libertosforever.telegram.utilits.*

class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {
    private lateinit var binding: FragmentEnterCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        AUTH = FirebaseAuth.getInstance()
        APP_ACTIVITY.title = phoneNumber
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            val string = binding.registerInputCode.text.toString()
            if (string.length >= 5) {
                enterCode()
            }
        })

    }

    private fun enterCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber
                dateMap[CHILD_USERNAME] = uid

                REF_DATABASE_ROOT_PHONES
                    .child(phoneNumber)
                    .setValue(uid)
                    .addOnFailureListener { showToast(it.message.toString()) }
                    .addOnSuccessListener {
                        REF_DATABASE_ROOT_USERS.child(uid).updateChildren(dateMap)
                            .addOnSuccessListener {
                                showToast("Welcome")
                                restartActivity()
                            }
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
            } else showToast(task.exception?.message.toString())
        }
    }
}