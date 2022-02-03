package com.libertosforever.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import com.libertosforever.telegram.R
import com.libertosforever.telegram.databinding.FragmentChangeBioBinding
import com.libertosforever.telegram.utilits.*

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
        REF_DATABASE_ROOT_USERS.child(CURRENT_UID).child(CHILD_BIO).setValue(newBio).addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(getString(R.string.toast_data_update))
                USER.bio = newBio
                activity?.supportFragmentManager?.popBackStack()
            }
        }

    }

}