package com.libertosforever.telegram.ui.message_recycle_view.view_holder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.CURRENT_UID
import com.libertosforever.telegram.ui.message_recycle_view.views.MessageView
import com.libertosforever.telegram.utilits.asTime

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {
    // user sent text message
    private val blockUserMessage: ConstraintLayout = view.findViewById(R.id.block_user_message)
    private val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
    private val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)

    // user received text message
    private val blockReceivedMessage: ConstraintLayout = view.findViewById(R.id.block_received_message)
    private val chatReceivedMessage: TextView = view.findViewById(R.id.chat_receive_message)
    private val chatReceivedMessageTime: TextView = view.findViewById(R.id.chat_receive_message_time)



    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}