package com.libertosforever.telegram.ui.fragments.message_recycle_view.views

interface MessageView {
    val id: String
    val from: String
    val timeStamp: String
    val fileUrl: String
    val text: String

    companion object {
        val MESSAGE_IMAGE: Int
            get() = 0

        val MESSAGE_TEXT: Int
            get() = 1
    }

    fun getTypeView(): Int
}







