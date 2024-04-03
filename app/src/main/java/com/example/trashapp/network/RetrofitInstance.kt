package com.example.trashapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// http://cgt.iptime.org:8080/
// http://ohs0407.iptime.org:8080
// http://10.0.2.2:8080/
object RetrofitInstance {
    private const val BASE_URL = "http://192.168.0.20:8080/"

    private val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance() : Retrofit{
        return client
    }

}