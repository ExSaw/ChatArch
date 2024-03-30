package com.rickrip.dictionary.chat_app.presentation.screen.chat

import android.graphics.Movie
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rickrip.dictionary.chat_app.core.util.IDispatchersProvider
import com.rickrip.dictionary.chat_app.data.local.ChatDatabase
import com.rickrip.dictionary.chat_app.data.local.entity.MessageEntity
import com.rickrip.dictionary.chat_app.data.remote.ChatApi
import com.rickrip.dictionary.chat_app.data.remote.ChatRemoteMediator
import com.rickrip.dictionary.chat_app.data.repository.ChatRepository
import com.rickrip.dictionary.chat_app.domain.mapper.toMessageModel
import com.rickrip.dictionary.chat_app.domain.model.MessageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@HiltViewModel
class ChatViewModel(
    private val dispatchers: IDispatchersProvider,
    private val api: ChatApi,
    private val database: ChatDatabase
) : ViewModel() {

    @OptIn(ExperimentalPagingApi::class)
    fun getMessages(
        userId: Int,
        contactId: Int
    ): Flow<PagingData<MessageModel>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = ChatRemoteMediator(
                userId = userId,
                contactId = contactId,
                charDb = database,
                chatApi = api,
            ),
            pagingSourceFactory = {
                database.dao.pagingSource(userId, contactId)
            }
        ).flow
            .map { pagingData ->
                pagingData.map { it.toMessageModel() }
            }.cachedIn(viewModelScope)
}