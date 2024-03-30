package com.rickrip.dictionary.chat_app.data.remote.dto

import androidx.annotation.Keep

@Keep
data class UserCredentialsDto(
    val login: String,
    val password: String,
)