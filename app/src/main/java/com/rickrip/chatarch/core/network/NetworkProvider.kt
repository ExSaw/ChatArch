package com.rickrip.dictionary.chat_app.core.network

import okhttp3.OkHttpClient
import javax.inject.Inject

class NetworkProvider @Inject constructor() {
    fun provideClient(
        headerInterceptor: HeaderModifierInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()
    }
}