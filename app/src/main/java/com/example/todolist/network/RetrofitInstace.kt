package com.example.todolist.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: TodoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")  // URL base da API pública
            .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON para objetos Kotlin
            .build()
            .create(TodoApiService::class.java)
    }
}
