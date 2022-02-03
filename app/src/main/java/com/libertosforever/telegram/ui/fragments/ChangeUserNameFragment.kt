package com.libertosforever.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import com.libertosforever.telegram.R
import com.libertosforever.telegram.databinding.FragmentChangeUserNameBinding
import com.libertosforever.telegram.utilits.*
import java.util.*

class ChangeUserNameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {
    private lateinit var mBinding: FragmentChangeUserNameBinding
    lateinit var mNewUserName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChangeUserNameBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mBinding.settingsInputUsername.setText(USER.username)

    }

    override fun change() {
        mNewUserName = mBinding.settingsInputUsername.text.toString().lowercase(Locale.getDefault())
        if (mNewUserName.isEmpty()) {
            showToast("Поле пустое")
        } else {
            REF_DATABASE_ROOT_USERNAMES.addListenerForSingleValueEvent(AppValueEventListener {
                if (it.hasChild(mNewUserName)) {
                    showToast(getString(R.string.user_exist))
                } else {
                    changeUserName()
                }
            })
            changeUserName()
        }
    }

    private fun changeUserName() {
        REF_DATABASE_ROOT_USERNAMES
            .child(mNewUserName)
            .setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername()
                }
            }
    }

    private fun updateCurrentUsername() {
        REF_DATABASE_ROOT_USERS
            .child(CURRENT_UID)
            .child(CHILD_USERNAME)
            .setValue(mNewUserName)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.toast_data_update))
                    deleteOldUsername()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deleteOldUsername() {
        REF_DATABASE_ROOT_USERNAMES.child(USER.username).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(getString(R.string.toast_data_update))
                activity?.supportFragmentManager?.popBackStack()
                USER.username = mNewUserName
            } else {
                showToast(it.exception?.message.toString())
            }
        }
    }
}