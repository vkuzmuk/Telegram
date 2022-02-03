package com.libertosforever.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import com.libertosforever.telegram.R
import com.libertosforever.telegram.databinding.FragmentChangeNameBinding
import com.libertosforever.telegram.utilits.*

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {
    lateinit var mBinding: FragmentChangeNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChangeNameBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        initFullnameList()
    }

    private fun initFullnameList() {
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1) {
            mBinding.settingsInputName.setText(fullnameList[0])
            mBinding.settingsInputSurname.setText(fullnameList[1])
        } else
            mBinding.settingsInputName.setText(fullnameList[0])
    }

    override fun change() {
        val name = mBinding.settingsInputName.text.toString()
        val surname = mBinding.settingsInputSurname.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullName = "$name $surname"
            REF_DATABASE_ROOT_USERS.child(CURRENT_UID).child(CHILD_FULL_NAME).setValue(fullName)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.toast_data_update))
                        USER.fullname = fullName
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                        activity?.supportFragmentManager?.popBackStack()

                    }
                }
        }
    }
}











