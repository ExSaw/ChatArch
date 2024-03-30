package com.rickrip.dictionary.chat_app.data.remote.dto

import androidx.annotation.Keep
import com.rickrip.dictionary.chat_app.data.local.entity.MessageEntity

@Keep
data class MessageDto(
    val id: Int,
    val contactId: Int,
    val date: String,
    val message: String,
) {
    fun toMessageEntity(
        userId: Int,
        contactId: Int,
    ): MessageEntity {
        return MessageEntity(
            id = id,
            date = date,
            message = message,
            contactId = contactId,
            userId = userId,
        )
    }
}