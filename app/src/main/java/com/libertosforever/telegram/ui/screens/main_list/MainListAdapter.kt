package com.libertosforever.telegram.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.libertosforever.telegram.R
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.ui.screens.groups.GroupChatFragment
import com.libertosforever.telegram.ui.screens.single_chat.SingleChatFragment
import com.libertosforever.telegram.utilits.TYPE_CHAT
import com.libertosforever.telegram.utilits.TYPE_GROUP
import com.libertosforever.telegram.utilits.downloadAndSetImage
import com.libertosforever.telegram.utilits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView

class MainListAdapter: RecyclerView.Adapter<MainListAdapter.MainListHolder>() {
    private var listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.main_list_item_name)
        val itemLastMessage: TextView = view.findViewById(R.id.main_list_last_message)
        val itemPhoto: CircleImageView = view.findViewById(R.id.main_list_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            when(listItems[holder.absoluteAdapterPosition].type) {
                TYPE_CHAT -> replaceFragment(SingleChatFragment(listItems[holder.absoluteAdapterPosition]))
                TYPE_GROUP ->    replaceFragment(GroupChatFragment(listItems[holder.absoluteAdapterPosition]))
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}