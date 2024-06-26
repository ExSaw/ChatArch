package com.rickrip.dictionary.chat_app.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rickrip.dictionary.chat_app.data.local.ChatDatabase
import com.rickrip.dictionary.chat_app.data.local.entity.MessageEntity
import com.rickrip.dictionary.chat_app.data.mapper.toMessageEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ChatRemoteMediator(
    private val userId: Int,
    private val contactId: Int,
    private val charDb: ChatDatabase,
    private val chatApi: ChatApi,
): RemoteMediator<Int, MessageEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null) {
                        1
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            val messages = chatApi.getContactMessages(
                contactId = contactId,
                page = loadKey,
                pageCount = state.config.pageSize
            )

            charDb.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    charDb.dao.clearAllUserContactMessages(userId, contactId)
                }
                val messageEntities = messages.map { it.toMessageEntity(userId, contactId) }
                charDb.dao.upsertMessages(messageEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = messages.isEmpty()
            )
        } catch(e: IOException) {
            MediatorResult.Error(e)
        } catch(e: HttpException) {
            MediatorResult.Error(e)
        }
    }

}
