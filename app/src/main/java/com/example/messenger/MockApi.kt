package com.example.messenger

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface MockApi {
    @GET("message")
    suspend fun getMessages():List<Message>

    @DELETE("message/{id}")
    fun deleteMessage(@Path("id") id: Int): Call<Void>
}
//парсинг делит и гет запросы