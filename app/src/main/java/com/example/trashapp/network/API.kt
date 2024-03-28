package com.example.trashapp.network

import com.example.trashapp.network.model.ApiList
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface API {
    @GET
    suspend fun getApiTest(
        @Url url : String, // Url에서 Retrofit이 인식 못하는 Url이 있을 경우 전체 Url을 넘겨줘야 함
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
        @Query("serviceKey", encoded = true) serviceKey: String,
    ): ApiList
}