package com.rickrip.dictionary.chat_app.domain.repository

import com.rickrip.dictionary.chat_app.core.util.Resource
import com.rickrip.dictionary.chat_app.domain.model.ContactModel
import com.rickrip.dictionary.chat_app.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface IChatRepository {

    fun login(
        isUsePreviousUser: Boolean,
        login: String,
        password: String
    ): Flow<Resource<UserModel>>

    suspend fun logout()

    fun fetch(): Flow<Resource<Unit>>

    fun getContacts(userId: Int?): Flow<Resource<List<ContactModel>>>

    fun addContact(userId: Int, contactModel: ContactModel): Flow<Resource<Boolean>>

    fun sendMessage(
        contactId: Int,
        message: String
    ): Flow<Resource<Unit>>

}