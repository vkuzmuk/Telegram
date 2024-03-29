package com.libertosforever.telegram.utilits

import com.libertosforever.telegram.database.*

enum class AppStates(val state: String) {
    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает...");

    companion object {
        fun updateState(appStates: AppStates) {
            if (AUTH.currentUser != null) {
                REF_DATABASE_ROOT_USERS
                    .child(CURRENT_UID)
                    .child(CHILD_STATUS)
                    .setValue(appStates.state)
                    .addOnSuccessListener {
                        USER.status = appStates.state
                    }
                    .addOnFailureListener {
                        showToast(it.message.toString())
                    }
            }
        }
    }
}