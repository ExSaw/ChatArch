package com.rickrip.dictionary.chat_app.presentation.event

import com.rickrip.dictionary.chat_app.domain.model.ContactModel

sealed interface UiEvent {

    data class OnLoginRequest(
        val isUsePrevUser: Boolean,
        val login: String,
        val password: String
    ) : UiEvent

    data class OnContactSelect(val contactId: Int) : UiEvent

    data class OnAddContact(val contactModel: ContactModel) : UiEvent

    data class OnSendMessage(val message: String) : UiEvent

    data object OnLogOut : UiEvent
}