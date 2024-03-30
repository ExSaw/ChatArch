package com.rickrip.dictionary.chat_app.core.util

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchersProvider {
    val main: CoroutineDispatcher
    val mainImmediate: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}