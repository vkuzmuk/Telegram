package com.libertosforever.telegram.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.CURRENT_UID
import com.libertosforever.telegram.database.REF_DATABASE_ROOT_USERNAMES
import com.libertosforever.telegram.database.USER
import com.libertosforever.telegram.database.updateCurrentUsername
import com.libertosforever.telegram.databinding.FragmentChangeUserNameBinding
import com.libertosforever.telegram.ui.screens.base.BaseChangeFragment
import com.libertosforever.telegram.utilits.AppValueEventListener
import com.libertosforever.telegram.utilits.showToast
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
                    updateCurrentUsername(mNewUserName)
                }
            }
    }
}