package com.rickrip.dictionary.chat_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rickrip.dictionary.chat_app.domain.model.ContactModel
import com.rickrip.dictionary.chat_app.domain.model.UserModel

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val token: String,
) {
    fun toUserModel() =
        UserModel(
            id = id,
            token = token
        )
}

@Entity
data class ContactEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val userId: Int,
)

@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = false)
    val id: ContactModel,
    val date: String,
    val message: String,
    val userId: Int,
    val contactId: Int,
)