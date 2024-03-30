package com.rickrip.dictionary.chat_app.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.rickrip.dictionary.chat_app.data.local.entity.ContactEntity
import com.rickrip.dictionary.chat_app.data.local.entity.MessageEntity
import com.rickrip.dictionary.chat_app.data.local.entity.UserEntity

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Upsert
    suspend fun upsertContacts(contacts: List<ContactEntity>)

    @Upsert
    suspend fun upsertMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM userentity LIMIT 1")
    fun getPreviousAuthorizedUser(): UserEntity?

    @Query("SELECT * FROM contactentity WHERE userId = :userId")
    fun getContacts(userId: Int): List<ContactEntity>

    @Query("SELECT * FROM messageentity WHERE userId = :userId AND contactId = :contactId")
    fun pagingSource(
        userId: Int,
        contactId: Int
    ): PagingSource<Int, MessageEntity>

    @Query("DELETE FROM messageentity WHERE userId = :userId AND contactId = :contactId")
    suspend fun clearAllUserContactMessages(
        userId: Int,
        contactId: Int
    )

    @Query("DELETE FROM userentity")
    suspend fun clearAllUsers()
}