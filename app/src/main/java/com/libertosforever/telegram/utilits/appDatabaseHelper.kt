package com.libertosforever.telegram.utilits

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.libertosforever.telegram.models.CommonModel
import com.libertosforever.telegram.models.User

lateinit var AUTH: FirebaseAuth
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User
lateinit var CURRENT_UID: String

lateinit var REF_DATABASE_ROOT_USERS: DatabaseReference
lateinit var REF_DATABASE_ROOT_USERNAMES: DatabaseReference
lateinit var REF_DATABASE_ROOT_PHONES: DatabaseReference
lateinit var REF_DATABASE_ROOT_PHONES_CONTACTS: DatabaseReference

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val FOLDER_PROFILE_IMAGE = "profile_image"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULL_NAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATUS = "status"


fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    val database =
        Firebase.database("https://telegram-5b102-default-rtdb.europe-west1.firebasedatabase.app")

    REF_DATABASE_ROOT_USERS = database.getReference(NODE_USERS)
    REF_DATABASE_ROOT_USERNAMES = database.getReference(NODE_USERNAMES)
    REF_DATABASE_ROOT_PHONES = database.getReference(NODE_PHONES)
    REF_DATABASE_ROOT_PHONES_CONTACTS = database.getReference(NODE_PHONES_CONTACTS)

    REF_STORAGE_ROOT = Firebase.storage.getReference(FOLDER_PROFILE_IMAGE)

    USER = User()
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
            USER = it.getValue(User::class.java) ?: User()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

@SuppressLint("Range")
fun initContacts() {
    if (checkPermissions(READ_CONTACTS)) {
        var arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullname = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullname
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesToDatabase(arrayContacts)
    }
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    REF_DATABASE_ROOT_PHONES.addListenerForSingleValueEvent(AppValueEventListener{
        it.children.forEach { snapshot ->  
            arrayContacts.forEach { contact ->
                if (snapshot.key == contact.phone) {
                    REF_DATABASE_ROOT_PHONES_CONTACTS
                        .child(CURRENT_UID)
                        .child(snapshot.value.toString())
                        .child(CHILD_ID)
                        .setValue(snapshot.value.toString())
                        .addOnFailureListener { showToast(it.message.toString()) }
                }
            }
        }
    })
}
