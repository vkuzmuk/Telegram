package com.libertosforever.telegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.libertosforever.telegram.R
import com.libertosforever.telegram.databinding.FragmentSingleChatBinding
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.models.UserModel
import com.libertosforever.telegram.ui.fragments.single_chat.SingleChatAdapter
import com.libertosforever.telegram.utilits.*
import io.reactivex.rxjava3.core.Single

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mBinding: FragmentSingleChatBinding
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppValueEventListener
    private var mListMessages = emptyList<CommonModel>()

    // for initializing
    private lateinit var mToolBarInfo: View
    private lateinit var toolBarImage: ImageView
    private lateinit var toolbarChatFullname: TextView
    private lateinit var toolbarChatStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolBarInfo = APP_ACTIVITY.findViewById(R.id.toolbar_info)
        toolBarImage = APP_ACTIVITY.findViewById(R.id.toolbar_chat_image)
        toolbarChatFullname = APP_ACTIVITY.findViewById(R.id.toolbar_chat_fullname)
        toolbarChatStatus = APP_ACTIVITY.findViewById(R.id.toolbar_chat_status)
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
        initToolbar()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = mBinding.chatRecyclerView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT_MESSAGES
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppValueEventListener { dataSnapshot ->
            mListMessages = dataSnapshot.children.map { it.getCommonModel() }
            mAdapter.setList(mListMessages)
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
        }
        mRefMessages.addValueEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolBarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }
        mRefUsers = REF_DATABASE_ROOT_USERS.child(contact.id)
        mRefUsers.addValueEventListener(mListenerInfoToolbar)
        mBinding.chatBtnSendMessage.setOnClickListener {
            val message = mBinding.chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessage(message, contact.id, TYPE_TEXT) {
                mBinding.chatInputMessage.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            toolbarChatFullname.text = contact.fullname
        } else toolbarChatFullname.text = mReceivingUser.fullname

        toolBarImage.downloadAndSetImage(mReceivingUser.photoUrl)
        toolbarChatStatus.text = mReceivingUser.status

    }

    override fun onPause() {
        super.onPause()
        mToolBarInfo.visibility = View.GONE
        mRefUsers.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }
}