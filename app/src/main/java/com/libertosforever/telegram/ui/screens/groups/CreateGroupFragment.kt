package com.libertosforever.telegram.ui.screens.groups

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.createGroupToDatabase
import com.libertosforever.telegram.databinding.FragmentCreateGroupBinding
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.ui.screens.base.BaseFragment
import com.libertosforever.telegram.ui.screens.main_list.MainListFragment
import com.libertosforever.telegram.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CreateGroupFragment(private var listContacts: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private lateinit var mBinding: FragmentCreateGroupBinding
    private var mUri = Uri.EMPTY

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
        hideKeyboard()
        initRecyclerView()
        mBinding.createGroupPhoto.setOnClickListener {
            addPhoto()
        }
        mBinding.createGroupBtnComplete.setOnClickListener {
            val nameGroup = mBinding.createGroupInputName.text.toString()
            if (nameGroup.isEmpty()) {
                showToast("Введите имя")
            } else {
                createGroupToDatabase(nameGroup, mUri, listContacts) {
                    replaceFragment(MainListFragment())
                }
            }
        }
        mBinding.createGroupInputName.requestFocus()
        mBinding.createGroupCounts.text = getPlurals(listContacts.size)
    }

    private fun initRecyclerView() {
        mRecyclerView = APP_ACTIVITY.findViewById(R.id.create_group_recycle_view)
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach {
            mAdapter.updateListItems(it)
        }
    }

    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            mUri = CropImage.getActivityResult(data).uri
            mBinding.createGroupPhoto.setImageURI(mUri)
        }
    }

}