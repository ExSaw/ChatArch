package com.rickrip.dictionary.chat_app.data.remote.dto

import androidx.annotation.Keep
import com.rickrip.dictionary.chat_app.data.local.entity.ContactEntity
import com.rickrip.dictionary.chat_app.domain.model.ContactModel

@Keep
data class ContactDto(
    val id: Int,
    val name: String,
) {
    fun toContactEntity(
        userId: Int,
    ): ContactEntity =
        ContactEntity(
            id = id,
            name = name,
            userId = userId,
        )

    fun toContactModel(): ContactModel =
        ContactModel(
            id = id,
            name = name
        )

}