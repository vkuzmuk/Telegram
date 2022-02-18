package com.libertosforever.telegram.ui.fragments.message_recycle_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.CURRENT_UID
import com.libertosforever.telegram.ui.fragments.message_recycle_view.views.MessageView
import com.libertosforever.telegram.utilits.asTime
import com.libertosforever.telegram.utilits.downloadAndSetImage

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view) {
    val blockReceivedImageMessage: ConstraintLayout = view.findViewById(R.id.block_received_image_message)
    val blockUserImageMessage: ConstraintLayout = view.findViewById(R.id.block_user_image_message)
    val chatUserImage: ImageView = view.findViewById(R.id.chat_user_image)
    val chatReceivedImage: ImageView = view.findViewById(R.id.chat_received_image)
    val chatUserImageMessageTime: TextView = view.findViewById(R.id.chat_user_image_message_time)
    val chatReceivedImageMessageTime: TextView = view.findViewById(R.id.chat_received_image_message_time)

    fun drawMessageImage(holder: HolderImageMessage, view: MessageView) {
        if (view.from == CURRENT_UID) {
            holder.blockReceivedImageMessage.visibility = View.GONE
            holder.blockUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(view.fileUrl)
            holder.chatUserImageMessageTime.text =
               view.timeStamp.asTime()
        } else {
            holder.blockReceivedImageMessage.visibility = View.VISIBLE
            holder.blockUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(view.fileUrl)
            holder.chatReceivedImageMessageTime.text =
                view.timeStamp.asTime()
        }
    }
}