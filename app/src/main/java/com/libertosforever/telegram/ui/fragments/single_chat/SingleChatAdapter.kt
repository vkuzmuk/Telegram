package com.libertosforever.telegram.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.database.CURRENT_UID
import com.libertosforever.telegram.utilits.asTime

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {
    private var mListMessagesCash = emptyList<CommonModel>()

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
        if (mListMessagesCash[position].from == CURRENT_UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceivedMessage.visibility = View.GONE
            holder.chatUserMessage.text = mListMessagesCash[position].text
            holder.chatUserMessageTime.text = mListMessagesCash[position].timeStamp.toString().asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceivedMessage.visibility = View.VISIBLE
            holder.chatReceivedMessage.text = mListMessagesCash[position].text
            holder.chatReceivedMessageTime.text = mListMessagesCash[position].timeStamp.toString().asTime()
        }
    }

    fun setList(list: List<CommonModel>) {
        mListMessagesCash = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mListMessagesCash.size
}









