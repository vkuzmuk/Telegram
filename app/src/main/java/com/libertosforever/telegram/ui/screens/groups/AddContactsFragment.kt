package com.libertosforever.telegram.ui.screens.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.*
import com.libertosforever.telegram.databinding.FragmentAddContactsBinding
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.utilits.APP_ACTIVITY
import com.libertosforever.telegram.utilits.AppValueEventListener
import com.libertosforever.telegram.utilits.hideKeyboard

class AddContactsFragment : Fragment(R.layout.fragment_add_contacts) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT_USERS
    private val mRefMessages = REF_DATABASE_ROOT_MESSAGES.child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()
    private lateinit var mBinding: FragmentAddContactsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddContactsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Добавить участника"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
        mBinding.addContactsBtnNext.setOnClickListener {
            listContacts.forEach {
                println(it.id)
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = APP_ACTIVITY.findViewById(R.id.add_contacts_recycle_view)
        mAdapter = AddContactsAdapter()
        // 1 query
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->
                // 2 query
                mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                    val newModel = dataSnapshot1.getCommonModel()
                    // 3 query
                    mRefMessages.child(model.id).limitToLast(1)
                        .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                            val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                            if (tempList.isEmpty()) {
                                newModel.lastMessage = "Чат очищен"
                            } else {
                                newModel.lastMessage = tempList[0].text
                            }
                            if (newModel.fullname.isEmpty()) {
                                newModel.fullname = newModel.phone
                            }
                            mAdapter.updateListItems(newModel)
                        })
                })
            }
        })
        mRecyclerView.adapter = mAdapter
    }
    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }
}




