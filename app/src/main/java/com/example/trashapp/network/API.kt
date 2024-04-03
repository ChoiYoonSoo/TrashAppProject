package com.example.trashapp.network

import com.example.trashapp.data.ApiValue
import com.example.trashapp.network.model.GpsList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface API {
    @GET("findTrashcans")
    suspend fun getApiTest(): List<ApiValue>

    @POST("getGPS")
    suspend fun getGPS(@Body responseGps: GpsList) : List<ApiValue>
}