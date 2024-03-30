package com.rickrip.dictionary.chat_app.domain.mapper

import com.rickrip.dictionary.chat_app.data.local.entity.ContactEntity
import com.rickrip.dictionary.chat_app.data.local.entity.MessageEntity
import com.rickrip.dictionary.chat_app.data.local.entity.UserEntity
import com.rickrip.dictionary.chat_app.domain.model.ContactModel
import com.rickrip.dictionary.chat_app.domain.model.MessageModel
import com.rickrip.dictionary.chat_app.domain.model.UserModel

fun UserEntity.toUserModel(): UserModel {
    return UserModel(
        id = id,
        token = token,
    )
}

fun ContactEntity.toContactModel(

): ContactModel {
    return ContactModel(
        id = id,
        name = name,
    )
}

fun MessageEntity.toMessageModel(): MessageModel {
    return MessageModel(
        id = id,
        date = date,
        message = message
    )
}