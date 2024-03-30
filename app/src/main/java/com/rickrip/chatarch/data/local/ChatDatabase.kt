package com.rickrip.dictionary.chat_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rickrip.dictionary.chat_app.data.local.entity.ContactEntity
import com.rickrip.dictionary.chat_app.data.local.entity.MessageEntity
import com.rickrip.dictionary.chat_app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class,ContactEntity::class,MessageEntity::class],
    version = 1
)
abstract class ChatDatabase: RoomDatabase() {
    abstract val dao: ChatDao
}