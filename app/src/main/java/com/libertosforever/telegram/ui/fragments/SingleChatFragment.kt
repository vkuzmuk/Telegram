package com.libertosforever.telegram.ui.fragments

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.libertosforever.telegram.R
import com.libertosforever.telegram.databinding.FragmentSingleChatBinding
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.utilits.APP_ACTIVITY

class SingleChatFragment(contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mBinding: FragmentSingleChatBinding
    private lateinit var mToolBarInfo: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolBarInfo = APP_ACTIVITY.findViewById<View>(R.id.toolbar_info)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSingleChatBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mToolBarInfo.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mToolBarInfo.visibility = View.GONE
    }
}