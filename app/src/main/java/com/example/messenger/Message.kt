package com.example.messenger



//Модель для парсинга преобразование в обьекты
data class Message(
    var id:Int,
    var name:String,
    var mess: String,
    var createdAt:String
)
