package com.libertosforever.telegram.utilits

enum class AppStates(val state: String) {
    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPiNG("печатает...");

    companion object {
        fun updateState(appStates: AppStates) {
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