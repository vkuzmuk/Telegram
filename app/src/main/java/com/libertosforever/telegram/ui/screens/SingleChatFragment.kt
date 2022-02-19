package com.libertosforever.telegram.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.*
import com.libertosforever.telegram.databinding.FragmentSingleChatBinding
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.models.UserModel
import com.libertosforever.telegram.ui.message_recycle_view.views.AppViewFactory
import com.libertosforever.telegram.ui.screens.single_chat.SingleChatAdapter
import com.libertosforever.telegram.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mBinding: FragmentSingleChatBinding
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 15
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    // for initializing
    private lateinit var mToolBarInfo: View
    private lateinit var toolBarImage: ImageView
    private lateinit var toolbarChatFullname: TextView
    private lateinit var toolbarChatStatus: TextView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var btnAttachFile: ImageView
    private lateinit var btnAttachImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
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
        initFields()
        initToolbar()
        initRecycleView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        mBottomSheetBehavior =
            BottomSheetBehavior.from(APP_ACTIVITY.findViewById(R.id.bottom_sheet_choice))
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = APP_ACTIVITY.findViewById(R.id.chat_swipe_refresh)
        mLayoutManager = LinearLayoutManager(this.context)
        mBinding.chatInputMessage.addTextChangedListener(AppTextWatcher {
            val string = mBinding.chatInputMessage.text.toString()
            if (string.isEmpty() || string == "Запись...") {
                mBinding.chatBtnSendMessage.visibility = View.GONE
                mBinding.chatBtnAttach.visibility = View.VISIBLE
                mBinding.chatBtnVoice.visibility = View.VISIBLE
            } else {
                mBinding.chatBtnSendMessage.visibility = View.VISIBLE
                mBinding.chatBtnAttach.visibility = View.GONE
                mBinding.chatBtnVoice.visibility = View.GONE
            }
        })
        mBinding.chatBtnAttach.setOnClickListener { attach() }

        CoroutineScope(Dispatchers.IO).launch {
            mBinding.chatBtnVoice.setOnTouchListener { view, motionEvent ->
                if (checkPermissions(RECORD_AUDIO)) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        mBinding.chatInputMessage.setText("Запись...")
                        mBinding.chatBtnVoice.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.blue_main
                            )
                        )
                        val messageKey = getMessageKey(contact.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            mBinding.chatInputMessage.setText("")
                            mBinding.chatBtnVoice.colorFilter = null
                            mAppVoiceRecorder.stopRecord() { file, messageKey ->
                                uploadFileToStorage(
                                    Uri.fromFile(file), messageKey, contact.id, TYPE_MESSAGE_VOICE)
                                mSmoothScrollToPosition = true
                            }
                        }
                }
                true
            }
        }
    }

    private fun attach() {
        btnAttachFile = APP_ACTIVITY.findViewById(R.id.btn_attach_file)
        btnAttachImage = APP_ACTIVITY.findViewById(R.id.btn_attach_image)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        btnAttachFile.setOnClickListener { attachFile() }
        btnAttachImage.setOnClickListener { attachImage() }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecycleView() {
        mRecyclerView = mBinding.chatRecyclerView
        mAdapter = SingleChatAdapter()

        mRefMessages = REF_DATABASE_ROOT_MESSAGES
            .child(CURRENT_UID)
            .child(contact.id)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(AppViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(AppViewFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                println(mRecyclerView.recycledViewPool.getRecycledViewCount(0))
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
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
            mSmoothScrollToPosition = true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(contact.id)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPosition = true
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)
                    val filename = getFileNameFromUrl(uri!!)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_FILE, filename)
                    mSmoothScrollToPosition = true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mToolBarInfo.visibility = View.GONE
        mRefUsers.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }
}