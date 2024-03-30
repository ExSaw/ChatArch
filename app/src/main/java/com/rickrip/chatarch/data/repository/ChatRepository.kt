package com.rickrip.dictionary.chat_app.data.repository

import com.rickrip.dictionary.chat_app.core.util.IDispatchersProvider
import com.rickrip.dictionary.chat_app.core.util.Resource
import com.rickrip.dictionary.chat_app.core.util.StandardDispatchers
import com.rickrip.dictionary.chat_app.data.local.ChatDao
import com.rickrip.dictionary.chat_app.data.remote.ChatApi
import com.rickrip.dictionary.chat_app.data.remote.dto.UserCredentialsDto
import com.rickrip.dictionary.chat_app.domain.mapper.toContactModel
import com.rickrip.dictionary.chat_app.domain.model.ContactModel
import com.rickrip.dictionary.chat_app.domain.model.UserModel
import com.rickrip.dictionary.chat_app.domain.repository.IChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class ChatRepository (
    private val dispatchers: IDispatchersProvider,
    private val api: ChatApi,
    private val dao: ChatDao,
) : IChatRepository {

    private companion object {
        const val DEFAULT_TIMEOUT = 10000L
    }

    override fun login(
        isUsePreviousUser: Boolean,
        login: String,
        password: String
    ): Flow<Resource<UserModel>> =
        flow {
            emit(Resource.Loading())
            runCatching {
                withTimeout(DEFAULT_TIMEOUT) {
                    if (isUsePreviousUser) {
                        dao.getPreviousAuthorizedUser()
                    } else {
                        api.login(UserCredentialsDto(login, password))
                            .toUserEntity()
                    }
                }
            }
                .onFailure {
                    emit(Resource.Error(it.message))
                }
                .onSuccess { userEntity ->
                    userEntity?.let {
                        dao.clearAllUsers()
                        dao.insertUser(it)
                        emit(Resource.Success(it.toUserModel()))
                    } ?: run {
                        emit(Resource.Error())
                    }
                }
        }

    override fun fetch(): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading())
            runCatching {
                withTimeout(DEFAULT_TIMEOUT) {
                    api.fetch()
                }
            }
                .onFailure { emit(Resource.Error(it.message)) }
                .onSuccess { emit(Resource.Success(Unit)) }
        }

    override fun getContacts(userId: Int?): Flow<Resource<List<ContactModel>>> =
        flow {
            emit(Resource.Loading())
            runCatching {
                withTimeout(DEFAULT_TIMEOUT) {
                    api.getContacts()
                }
            }
                .onFailure {
                    userId?.let {
                        emit(
                            Resource.Success(
                                dao.getContacts(it).map { contactEntity ->
                                    contactEntity.toContactModel()
                                }
                            )
                        )
                    }
                        ?: run {
                            emit(Resource.Error(it.message))
                        }
                }
                .onSuccess {
                    userId?.let { userId ->
                        dao.upsertContacts(it.map { it.toContactEntity(userId) })
                    }
                    emit(
                        Resource.Success(
                            it.map { it.toContactModel() }
                        )
                    )
                }
        }

    override fun addContact(
        userId: Int,
        contactModel: ContactModel
    ): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            runCatching {
                withTimeout(DEFAULT_TIMEOUT) {
                    api.addContact(contactModel.toContactDto())
                }
            }
                .onFailure { emit(Resource.Error(it.message)) }
                .onSuccess { isContactAdded ->
                    if (isContactAdded) {
                        dao.insertContact(contactModel.toContactEntity(userId))
                    }
                    emit(Resource.Success(isContactAdded))
                }
        }

    override fun sendMessage(
        contactId: Int,
        message: String
    ): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading())
            runCatching {
                withTimeout(DEFAULT_TIMEOUT) {
                    api.sendMessage(contactId, message)
                }
            }
                .onFailure { emit(Resource.Error(it.message)) }
                .onSuccess { emit(Resource.Success(Unit)) }
        }

    override suspend fun logout() {
        withContext(dispatchers.io + NonCancellable) {
            dao.clearAllUsers()
        }
    }
}