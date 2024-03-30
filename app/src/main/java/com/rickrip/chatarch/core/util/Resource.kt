package com.rickrip.dictionary.chat_app.core.util

sealed class Resource<T>(
    open val data: T? = null,
    open val message: String? = null,
    open val exception: Exception? = null
) {
    data class Loading<T>(override val data: T? = null): Resource<T>(data)

    data class Success<T>(override val data: T?): Resource<T>(data)

    data class Error<T>(
        override val message: String? = null,
        override val data: T? = null,
        override val exception: Exception? = null
    ): Resource<T>(data, message, exception)
}