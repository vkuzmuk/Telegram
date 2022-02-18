package com.libertosforever.telegram.ui.screens

import android.os.Bundle
import android.view.*
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.*
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
            setNameToDatabase(fullName)
        }
    }
}











