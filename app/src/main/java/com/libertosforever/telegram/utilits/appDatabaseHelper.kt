package com.libertosforever.telegram.utilits

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.models.UserModel

lateinit var AUTH: FirebaseAuth
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel
lateinit var CURRENT_UID: String

lateinit var REF_DATABASE_ROOT_USERS: DatabaseReference
lateinit var REF_DATABASE_ROOT_USERNAMES: DatabaseReference
lateinit var REF_DATABASE_ROOT_PHONES: DatabaseReference
lateinit var REF_DATABASE_ROOT_PHONES_CONTACTS: DatabaseReference
lateinit var REF_DATABASE_ROOT_MESSAGES: DatabaseReference

const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val NODE_MESSAGES = "messages"
const val FOLDER_PROFILE_IMAGE = "profile_image"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULL_NAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATUS = "status"
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"


fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    val database =
        Firebase.database("https://telegram-5b102-default-rtdb.europe-west1.firebasedatabase.app")

    REF_DATABASE_ROOT_USERS = database.getReference(NODE_USERS)
    REF_DATABASE_ROOT_USERNAMES = database.getReference(NODE_USERNAMES)
    REF_DATABASE_ROOT_PHONES = database.getReference(NODE_PHONES)
    REF_DATABASE_ROOT_PHONES_CONTACTS = database.getReference(NODE_PHONES_CONTACTS)
    REF_DATABASE_ROOT_MESSAGES = database.getReference(NODE_MESSAGES)

    REF_STORAGE_ROOT = Firebase.storage.getReference(FOLDER_PROFILE_IMAGE)

    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT_USERS
        .child(CURRENT_UID)
        .child(CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT_USERS.child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT_PHONES.addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone) {
                        REF_DATABASE_ROOT_PHONES_CONTACTS
                            .child(CURRENT_UID)
                            .child(snapshot.value.toString())
                            .child(CHILD_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }

                        REF_DATABASE_ROOT_PHONES_CONTACTS
                            .child(CURRENT_UID)
                            .child(snapshot.value.toString())
                            .child(CHILD_FULL_NAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(message: String, receivingUserId: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$receivingUserId/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT_MESSAGES.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT_MESSAGES
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
