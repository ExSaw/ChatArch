package com.rickrip.dictionary.chat_app.core.network

interface ITokenHandler {
    fun getSavedToken(): String?
    fun updateSavedToken(token: String?)
    fun clearSavedToken()
}

class TokenHandler : ITokenHandler {
    private var _token: String? = null

    override fun getSavedToken() = _token

    override fun updateSavedToken(token: String?) {
        _token = token
    }

    override fun clearSavedToken() {
        _token = null
    }
}