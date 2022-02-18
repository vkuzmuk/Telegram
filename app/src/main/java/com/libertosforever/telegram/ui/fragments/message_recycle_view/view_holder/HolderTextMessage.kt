package com.libertosforever.telegram.ui.fragments.message_recycle_view.view_holder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view) {
    // user sent text message
    val blockUserMessage: ConstraintLayout = view.findViewById(R.id.block_user_message)
    val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
    val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)

    // user received text message
    val blockReceivedMessage: ConstraintLayout = view.findViewById(R.id.block_received_message)
    val chatReceivedMessage: TextView = view.findViewById(R.id.chat_receive_message)
    val chatReceivedMessageTime: TextView = view.findViewById(R.id.chat_receive_message_time)
}