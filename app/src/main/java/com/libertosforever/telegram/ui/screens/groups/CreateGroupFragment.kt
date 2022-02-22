package com.libertosforever.telegram.ui.screens.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.databinding.FragmentCreateGroupBinding
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.ui.screens.base.BaseFragment
import com.libertosforever.telegram.utilits.APP_ACTIVITY
import com.libertosforever.telegram.utilits.hideKeyboard
import com.libertosforever.telegram.utilits.showToast

class CreateGroupFragment(private var listContacts: List<CommonModel>): BaseFragment(R.layout.fragment_create_group) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mListItems = listOf<CommonModel>()
    private lateinit var mBinding: FragmentCreateGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
        mBinding.createGroupBtnComplete.setOnClickListener {
            showToast("CLICK")
        }
        mBinding.createGroupInputName.requestFocus()
    }

    private fun initRecyclerView() {
        mRecyclerView = APP_ACTIVITY.findViewById(R.id.create_group_recycle_view)
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach {
            mAdapter.updateListItems(it)
        }
    }
}