package com.libertosforever.telegram.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.*
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.utilits.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT_USERS
    private val mRefMessages = REF_DATABASE_ROOT_MESSAGES.child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = APP_ACTIVITY.findViewById(R.id.main_list_recycle_view)
        mAdapter = MainListAdapter()
        // 1 query
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->
                when (model.type) {
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }
            }
        })
        mRecyclerView.adapter = mAdapter
    }

    private fun showGroup(model: CommonModel) {
        REF_DATABASE_ROOT
            .child(NODE_GROUPS)
            .child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                val newModel = dataSnapshot1.getCommonModel()
                // 3 query
                REF_DATABASE_ROOT
                    .child(NODE_GROUPS)
                    .child(model.id)
                    .child(NODE_MESSAGES)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                        if (tempList.isEmpty()) {
                            newModel.lastMessage = "Чат очищен"
                        } else {
                            newModel.lastMessage = tempList[0].text
                        }
                        newModel.type = TYPE_GROUP
                        mAdapter.updateListItems(newModel)
                    })
            })
    }

    private fun showChat(model: CommonModel) {
        // 2 query
        mRefUsers.child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
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
                        newModel.type = TYPE_CHAT
                        mAdapter.updateListItems(newModel)
                    })
            })
    }
}




