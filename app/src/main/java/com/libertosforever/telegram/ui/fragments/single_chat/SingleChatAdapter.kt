package com.libertosforever.telegram.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.database.CURRENT_UID
import com.libertosforever.telegram.utilits.DiffUtilCallback
import com.libertosforever.telegram.utilits.asTime

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {
    private var mListMessagesCache = emptyList<CommonModel>()
    private lateinit var mDiffResult: DiffUtil.DiffResult

    class SingleChatHolder(view: View): RecyclerView.ViewHolder(view) {
        // user sent message
        val blockUserMessage: ConstraintLayout = view.findViewById(R.id.block_user_message)
        val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
        val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)

        // user received message
        val blockReceivedMessage: ConstraintLayout = view.findViewById(R.id.block_received_message)
        val chatReceivedMessage: TextView = view.findViewById(R.id.chat_receive_message)
        val chatReceivedMessageTime: TextView = view.findViewById(R.id.chat_receive_message_time)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
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

    fun setList(list: List<CommonModel>) {
        mDiffResult.dispatchUpdatesTo(this)
        mListMessagesCache = list
    }

    fun addItem(item: CommonModel) {
        val newList = mutableListOf<CommonModel>()
        newList.addAll(mListMessagesCache)

        if (!newList.contains(item)) newList.add(item)

        newList.sortBy { it.timeStamp.toString() }
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(mListMessagesCache, newList))
        mDiffResult.dispatchUpdatesTo(this)
        mListMessagesCache = newList
    }

}









