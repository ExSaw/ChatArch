package com.rickrip.dictionary.chat_app.presentation.state

sealed class Stage {
    data object AuthStage : Stage()
    data object ContactsStage : Stage()
    data class ChatStage(val userId: Int, val contactId: Int): Stage()
}