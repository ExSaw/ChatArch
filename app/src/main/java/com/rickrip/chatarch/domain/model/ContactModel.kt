package com.rickrip.dictionary.chat_app.domain.model

import com.rickrip.dictionary.chat_app.data.local.entity.ContactEntity
import com.rickrip.dictionary.chat_app.data.remote.dto.ContactDto

data class ContactModel(
    val id: Int,
    val name: String,
) {
    fun toContactDto() =
        ContactDto(
            id = id,
            name = name
        )

    fun toContactEntity(userId: Int) =
        ContactEntity(
            id = id,
            name = name,
            userId = userId
        )
}