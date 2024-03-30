package com.rickrip.dictionary.chat_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickrip.dictionary.chat_app.core.network.ITokenHandler
import com.rickrip.dictionary.chat_app.core.network.TokenHandler
import com.rickrip.dictionary.chat_app.core.util.IDispatchersProvider
import com.rickrip.dictionary.chat_app.core.util.Resource
import com.rickrip.dictionary.chat_app.domain.model.ContactModel
import com.rickrip.dictionary.chat_app.domain.repository.IChatRepository
import com.rickrip.dictionary.chat_app.presentation.event.SystemMessage
import com.rickrip.dictionary.chat_app.presentation.event.UiEvent
import com.rickrip.dictionary.chat_app.presentation.state.Stage
import com.rickrip.dictionary.chat_app.presentation.state.UserDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatchers: IDispatchersProvider,
    private val repository: IChatRepository,
    private val tokenHandler: ITokenHandler,
) : ViewModel() {

    private companion object {
        const val AUTO_FETCH_MESSAGES_TIMEOUT = 5000L
    }

    private val _stageState = MutableStateFlow<Stage>(Stage.AuthStage)
    val stageState = _stageState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _systemMessagesBus = MutableSharedFlow<SystemMessage>()
    val systemMessagesBus = _systemMessagesBus.asSharedFlow()

    private val _userDataState = MutableStateFlow(UserDataState())
    val userDataState = _userDataState.asStateFlow()

    private var autoFetchJob: Job? = null

    init {
        viewModelScope.launch(dispatchers.default) {
            uiEvent.collect { event ->
                when (event) {
                    is UiEvent.OnLoginRequest -> {
                        login(event.isUsePrevUser, event.login, event.password)
                    }

                    is UiEvent.OnContactSelect -> {
                        userDataState.first().userModel?.let { userModel ->
                            setStage(
                                Stage.ChatStage(
                                    userId = userModel.id,
                                    contactId = event.contactId
                                )
                            )
                        } ?: run {
                            sendEvent(SystemMessage.CONTACT_NOT_FOUND)
                        }
                    }

                    is UiEvent.OnAddContact -> {
                        userDataState.first().userModel?.id?.let { userId ->
                            addContact(
                                userId = userId,
                                contactModel = event.contactModel
                            )
                        } ?: run {
                            sendEvent(SystemMessage.CONTACT_ADD_ERROR)
                        }
                    }

                    is UiEvent.OnSendMessage -> {
                        sendMessage(event.message)
                    }

                    is UiEvent.OnLogOut -> {
                        logout()
                    }
                }
            }
        }
        viewModelScope.launch(dispatchers.default) {
            stageState.collectLatest { stage ->
                when (stage) {
                    is Stage.ChatStage -> {
                        autoFetchJob?.cancelAndJoin()
                        autoFetchJob = autoFetchMessagesJob()
                    }

                    else -> {
                        autoFetchJob?.cancel()
                    }
                }
            }
        }
    }

    fun sendUiEvent(uiEvent: UiEvent) {
        viewModelScope.launch(dispatchers.default) {
            _uiEvent.emit(uiEvent)
        }
    }

    private fun CoroutineScope.login(
        isUsePrevUser: Boolean,
        login: String,
        password: String
    ) = launch(dispatchers.io) {
        repository.login(isUsePrevUser, login, password)
            .collectLatest { result ->
                when (result) {
                    is Resource.Success -> {

                        tokenHandler.updateSavedToken(result.data?.token)

                        _userDataState.emit(UserDataState(userModel = result.data))

                        setStage(Stage.ContactsStage)

                        getContacts(result.data?.id)
                    }

                    is Resource.Error -> {
                        sendEvent(SystemMessage.LOGIN_PROBLEM)
                    }

                    is Resource.Loading -> {
                        sendEvent(SystemMessage.LOADING)
                    }
                }
            }
    }

    private fun CoroutineScope.getContacts(userId: Int?) =
        launch(dispatchers.io) {
            repository.getContacts(userId)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _userDataState.update {
                                it.copy(userContacts = result.data)
                            }
                        }

                        is Resource.Error -> {
                            sendEvent(SystemMessage.NO_CONNECTION)
                        }

                        is Resource.Loading -> {
                            sendEvent(SystemMessage.LOADING)
                        }
                    }
                }
        }

    /**
     * * check for new messages
     */
    private fun CoroutineScope.fetchMessages(): Deferred<Unit> =
        async(dispatchers.io) {
            repository.fetch()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            sendEvent(SystemMessage.NEW_INCOME_MESSAGE)
                            sendEvent(SystemMessage.NEED_UPDATE)
                        }

                        is Resource.Error -> {
                            sendEvent(SystemMessage.FETCH_PROBLEM)
                        }

                        is Resource.Loading -> {
                            sendEvent(SystemMessage.LOADING)
                        }
                    }
                }
        }

    private fun CoroutineScope.sendMessage(message: String) =
        launch(dispatchers.io) {
            (stageState.first() as? Stage.ChatStage)
                ?.contactId
                ?.let { contactId ->
                    withContext(NonCancellable) {
                        repository.sendMessage(contactId, message)
                            .collectLatest { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        sendEvent(SystemMessage.NEED_UPDATE)
                                    }

                                    is Resource.Error -> {
                                        sendEvent(SystemMessage.MESSAGE_SEND_PROBLEM)
                                    }

                                    is Resource.Loading -> {
                                        sendEvent(SystemMessage.LOADING)
                                    }
                                }
                            }
                    }
                }
        }

    private fun CoroutineScope.addContact(
        userId: Int,
        contactModel: ContactModel,
    ) = launch(dispatchers.io) {
        repository.addContact(
            userId = userId,
            contactModel = contactModel
        ).collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    sendEvent(SystemMessage.NEW_CONTACT_ADDED)
                }

                is Resource.Error -> {
                    sendEvent(SystemMessage.NO_CONNECTION)
                }

                is Resource.Loading -> {
                    sendEvent(SystemMessage.LOADING)
                }
            }
        }
    }

    private fun CoroutineScope.autoFetchMessagesJob(): Job =
        launch(dispatchers.default) {
            while (true) {
                delay(AUTO_FETCH_MESSAGES_TIMEOUT)
                fetchMessages().await()
            }
        }

    private suspend fun setStage(stage: Stage) {
        withContext(dispatchers.default) {
            _stageState.emit(stage)
        }
    }

    private suspend fun sendEvent(systemMessage: SystemMessage) {
        withContext(dispatchers.default) {
            _systemMessagesBus.emit(systemMessage)
        }
    }

    private fun CoroutineScope.logout() =
        launch(dispatchers.default) {
            tokenHandler.clearSavedToken()
            repository.logout()
            setStage(Stage.AuthStage)
        }
}