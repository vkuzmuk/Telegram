package com.libertosforever.telegram.ui.screens.groups

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.ui.message_recycle_view.view_holder.AppHolderFactory
import com.libertosforever.telegram.ui.message_recycle_view.view_holder.MessageHolder
import com.libertosforever.telegram.ui.message_recycle_view.views.MessageView

class GroupChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListMessagesCache = mutableListOf<MessageView>()
    private var mListHolders = mutableListOf<MessageHolder>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(mListMessagesCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(mListMessagesCache[holder.absoluteAdapterPosition]) // replace later
        mListHolders.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        mListHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun onDestroy() {
        mListHolders.forEach {
            it.onDetach()
        }
    }
}








