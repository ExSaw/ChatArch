package com.rickrip.dictionary.chat_app.data.remote.dto

import androidx.annotation.Keep
import com.rickrip.dictionary.chat_app.data.local.entity.UserEntity

@Keep
data class UserDto(
    val id: Int,
    val token: String,
) {
    fun toUserEntity() =
        UserEntity(
            id = id,
            token = token
        )
}