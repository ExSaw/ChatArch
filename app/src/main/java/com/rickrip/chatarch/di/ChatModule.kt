package com.rickrip.dictionary.chat_app.di

import android.app.Application
import androidx.room.Room
import com.rickrip.dictionary.chat_app.core.network.HeaderModifierInterceptor
import com.rickrip.dictionary.chat_app.core.network.ITokenHandler
import com.rickrip.dictionary.chat_app.core.network.NetworkProvider
import com.rickrip.dictionary.chat_app.core.network.TokenHandler
import com.rickrip.dictionary.chat_app.core.util.IDispatchersProvider
import com.rickrip.dictionary.chat_app.core.util.StandardDispatchers
import com.rickrip.dictionary.chat_app.data.local.ChatDatabase
import com.rickrip.dictionary.chat_app.data.remote.ChatApi
import com.rickrip.dictionary.chat_app.data.repository.ChatRepository
import com.rickrip.dictionary.chat_app.domain.repository.IChatRepository
import com.rickrip.dictionary.chat_app.presentation.router.ScreenRouter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideRepository(
        dispatchers: IDispatchersProvider,
        db: ChatDatabase,
        api: ChatApi
    ): IChatRepository {
        return ChatRepository(dispatchers, api, db.dao)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): ChatDatabase {
        return Room.databaseBuilder(
            app, ChatDatabase::class.java, "chat_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideClient(
        headerModifierInterceptor: HeaderModifierInterceptor
    ):OkHttpClient {
        return NetworkProvider().provideClient(headerModifierInterceptor)
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(
        tokenHandler: ITokenHandler
    ):HeaderModifierInterceptor {
        return HeaderModifierInterceptor(tokenHandler)
    }

    @Provides
    @Singleton
    fun provideApi(
        client: OkHttpClient
    ): ChatApi {
        return Retrofit.Builder()
            .baseUrl(ChatApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDispatchers(): IDispatchersProvider = StandardDispatchers()

    @Provides
    @Singleton
    fun provideScreenRouter(): ScreenRouter = ScreenRouter()

    @Provides
    @Singleton
    fun provideTokenHandler(): ITokenHandler = TokenHandler()
}