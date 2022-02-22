package com.libertosforever.telegram.ui.screens.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.libertosforever.telegram.R
import com.libertosforever.telegram.database.CURRENT_UID
import com.libertosforever.telegram.database.REF_DATABASE_ROOT_PHONES_CONTACTS
import com.libertosforever.telegram.database.REF_DATABASE_ROOT_USERS
import com.libertosforever.telegram.database.getCommonModel
import com.libertosforever.telegram.databinding.FragmentContactsBinding
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.ui.screens.base.BaseFragment
import com.libertosforever.telegram.ui.screens.single_chat.SingleChatFragment
import com.libertosforever.telegram.utilits.APP_ACTIVITY
import com.libertosforever.telegram.utilits.AppValueEventListener
import com.libertosforever.telegram.utilits.downloadAndSetImage
import com.libertosforever.telegram.utilits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mBinding: FragmentContactsBinding
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentContactsBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = mBinding.contactsRecycleView
        mRefContacts = REF_DATABASE_ROOT_PHONES_CONTACTS.child(CURRENT_UID)
        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT_USERS.child(model.id)
                mRefUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()
                    if (contact.fullname.isEmpty()) {
                        holder.name.text = model.fullname
                    } else holder.name.text = contact.fullname

                    holder.status.text = contact.status
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener { replaceFragment(SingleChatFragment(model))}
                }

                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers] = mRefUsersListener
            }
        }
        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()

    }

    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contact_fullname)
        val status: TextView = view.findViewById(R.id.contact_status)
        val photo: CircleImageView = view.findViewById(R.id.contact_photo)
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()

        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}

