package com.example.trashapp.repository

import com.example.trashapp.data.SignUp
import com.example.trashapp.network.API
import com.example.trashapp.network.RetrofitInstance
import com.example.trashapp.network.model.GpsList

class NetWorkRepository {

    private val client = RetrofitInstance.getInstance().create(API::class.java)

    // 위도 경도 API 호출
    suspend fun getApiTest() = client.getApiTest()

    suspend fun getGPS(responseGps : GpsList) = client.getGPS(responseGps)

    suspend fun validateDuplicateEmail(email : String) = client.validateDuplicateEmail(email)

    suspend fun validateDuplicateNick(nickname : String) = client.validateDuplicateNick(nickname)

    suspend fun signUp(signUp: SignUp) = client.signUp(signUp)
}