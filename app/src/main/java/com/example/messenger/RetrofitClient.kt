package com.example.messenger

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//Настройка обьекта для парсинга. Реализует паттерн одиночки
object RetrofitClient {
    private const val BASE_URL = "https://6662f78762966e20ef0ac433.mockapi.io/apiWhatsApp/"

    val instance: MockApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MockApi::class.java)
    }
}
