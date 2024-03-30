package com.rickrip.dictionary.chat_app.presentation.state

import com.rickrip.dictionary.chat_app.domain.model.ContactModel
import com.rickrip.dictionary.chat_app.domain.model.UserModel

data class UserDataState(
    val userModel: UserModel? = null,
    val userContacts: List<ContactModel>? = null,
)