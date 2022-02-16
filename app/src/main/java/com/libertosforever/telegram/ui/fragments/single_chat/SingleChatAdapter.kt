package com.libertosforever.telegram.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.CURRENT_UID
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.utilits.TYPE_MESSAGE_IMAGE
import com.libertosforever.telegram.utilits.TYPE_MESSAGE_TEXT
import com.libertosforever.telegram.utilits.asTime
import com.libertosforever.telegram.utilits.downloadAndSetImage

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {
    private var mListMessagesCache = mutableListOf<CommonModel>()

    class SingleChatHolder(view: View): RecyclerView.ViewHolder(view) {
        // user sent text message
        val blockUserMessage: ConstraintLayout = view.findViewById(R.id.block_user_message)
        val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
        val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)

        // user received text message
        val blockReceivedMessage: ConstraintLayout = view.findViewById(R.id.block_received_message)
        val chatReceivedMessage: TextView = view.findViewById(R.id.chat_receive_message)
        val chatReceivedMessageTime: TextView = view.findViewById(R.id.chat_receive_message_time)

        //Image message
        val blockReceivedImageMessage: ConstraintLayout = view.findViewById(R.id.block_received_image_message)
        val blockUserImageMessage: ConstraintLayout = view.findViewById(R.id.block_user_image_message)
        val chatUserImage: ImageView = view.findViewById(R.id.chat_user_image)
        val chatReceivedImage: ImageView = view.findViewById(R.id.chat_received_image)
        val chatUserImageMessageTime: TextView = view.findViewById(R.id.chat_user_image_message_time)
        val chatReceivedImageMessageTime: TextView = view.findViewById(R.id.chat_received_image_message_time)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when (mListMessagesCache[position].type) {
            TYPE_MESSAGE_TEXT -> {drawMessageText(holder, position)}
            TYPE_MESSAGE_IMAGE -> {drawMessageImage(holder, position)}
        }
    }

    private fun drawMessageImage(holder: SingleChatHolder, position: Int) {
        holder.blockUserMessage.visibility = View.GONE
        holder.blockReceivedMessage.visibility = View.GONE
        if (mListMessagesCache[position].from == CURRENT_UID) {
            holder.blockReceivedImageMessage.visibility = View.GONE
            holder.blockUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mListMessagesCache[position].fileUrl)
            holder.chatUserImageMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blockReceivedImageMessage.visibility = View.VISIBLE
            holder.blockUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(mListMessagesCache[position].fileUrl)
            holder.chatReceivedImageMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    private fun drawMessageText(holder: SingleChatHolder, position: Int) {
        holder.blockReceivedImageMessage.visibility = View.GONE
        holder.blockUserImageMessage.visibility = View.GONE
        if (mListMessagesCache[position].from == CURRENT_UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceivedMessage.visibility = View.GONE
            holder.chatUserMessage.text = mListMessagesCache[position].text
            holder.chatUserMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceivedMessage.visibility = View.VISIBLE
            holder.chatReceivedMessage.text = mListMessagesCache[position].text
            holder.chatReceivedMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    fun addItemToBottom(  item: CommonModel, onSuccess:() -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(  item: CommonModel, onSuccess:() -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}








