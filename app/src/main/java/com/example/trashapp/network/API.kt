package com.example.trashapp.network

import com.example.trashapp.network.model.CampsiteItem
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    @GET("basedList")
    suspend fun getCampList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") type: String
    ): CampsiteItem
}