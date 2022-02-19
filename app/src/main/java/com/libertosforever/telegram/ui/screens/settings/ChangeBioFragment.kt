package com.libertosforever.telegram.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.USER
import com.libertosforever.telegram.database.setBioToDatabase
import com.libertosforever.telegram.databinding.FragmentChangeBioBinding
import com.libertosforever.telegram.ui.screens.BaseChangeFragment

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
    private lateinit var mBinding: FragmentChangeBioBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChangeBioBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mBinding.settingsInputBio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = mBinding.settingsInputBio.text.toString()
        setBioToDatabase(newBio)
    }
}