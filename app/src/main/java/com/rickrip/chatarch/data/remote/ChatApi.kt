package com.rickrip.dictionary.chat_app.data.remote

import com.rickrip.dictionary.chat_app.data.remote.dto.ContactDto
import com.rickrip.dictionary.chat_app.data.remote.dto.MessageDto
import com.rickrip.dictionary.chat_app.data.remote.dto.UserCredentialsDto
import com.rickrip.dictionary.chat_app.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {

    companion object {
        const val BASE_URL = "https://api.mychat.com/v2/"
    }

    @POST("login")
    suspend fun login(@Body userCredentialsDto: UserCredentialsDto): UserDto

    @GET("fetch")
    suspend fun fetch(): Boolean

    @GET("contacts")
    suspend fun getContacts(): List<ContactDto>

    @POST("add_contact")
    suspend fun addContact(
        @Body contactDto: ContactDto
    ): Boolean

    @GET("contact_messages")
    suspend fun getContactMessages(
        @Query("contact_id") contactId: Int,
        @Query("page") page: Int,
        @Query("per_page") pageCount: Int
    ): List<MessageDto>

    @POST("message/{contact_id}")
    suspend fun sendMessage(
        @Path("contact_id") contactId: Int,
        @Body message: String,
    )
}