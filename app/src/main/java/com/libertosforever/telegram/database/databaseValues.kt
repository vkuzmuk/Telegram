package com.libertosforever.telegram.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.libertosforever.telegram.models.UserModel

lateinit var AUTH: FirebaseAuth
lateinit var USER: UserModel
lateinit var CURRENT_UID: String

lateinit var REF_DATABASE_ROOT_USERS: DatabaseReference
lateinit var REF_DATABASE_ROOT_USERNAMES: DatabaseReference
lateinit var REF_DATABASE_ROOT_PHONES: DatabaseReference
lateinit var REF_DATABASE_ROOT_PHONES_CONTACTS: DatabaseReference
lateinit var REF_DATABASE_ROOT_MESSAGES: DatabaseReference

lateinit var REF_STORAGE_ROOT_PROFILE_IMAGE: StorageReference
lateinit var REF_STORAGE_ROOT_FILES: StorageReference
lateinit var REF_STORAGE_ROOT: StorageReference

const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val NODE_MESSAGES = "messages"

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_FILES = "messages_files"

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
const val CHILD_FILE_URL = "fileUrl"