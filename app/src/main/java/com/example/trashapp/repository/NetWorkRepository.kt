package com.example.trashapp.repository

import com.example.trashapp.network.API
import com.example.trashapp.network.RetrofitInstance

class NetWorkRepository {

    private val client = RetrofitInstance.getInstance().create(API::class.java)

    // 위도 경도 API 호출
    suspend fun getApiTest() = client.getApiTest()

}