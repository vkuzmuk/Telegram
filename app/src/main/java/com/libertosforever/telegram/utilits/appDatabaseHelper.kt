package com.libertosforever.telegram.utilits

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.libertosforever.telegram.models.User

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT_USERS: DatabaseReference
lateinit var REF_DATABASE_ROOT_USERNAMES: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User
lateinit var CURRENT_UID: String

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
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
