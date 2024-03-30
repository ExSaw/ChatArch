package com.rickrip.dictionary.chat_app.core.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.userAgent

class HeaderModifierInterceptor(
    private val tokenHandler: ITokenHandler
) : Interceptor {

    private companion object {
        const val AUTHORIZATION = "AUTHORIZATION"
    }

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (chain.request().headers["Content-Type"] == null) {
            requestBuilder.header("Content-Type", "application/json")
        }
        requestBuilder.addHeader("Connection", "close")
        tokenHandler.getSavedToken()?.let { jwtToken ->
            requestBuilder.addHeader(AUTHORIZATION, "Bearer $jwtToken")
        }
        return chain.proceed(requestBuilder.build())
    }
}