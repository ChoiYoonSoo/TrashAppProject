package com.example.trashapp.network

import com.example.trashapp.data.ApiValue
import com.example.trashapp.network.model.ApiList
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface API {
    @GET("findTrashcans")
    suspend fun getApiTest(): List<ApiValue>
}