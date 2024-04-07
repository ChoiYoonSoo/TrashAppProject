package com.example.trashapp.repository

import com.example.trashapp.data.SignUp
import com.example.trashapp.network.API
import com.example.trashapp.network.RetrofitInstance
import com.example.trashapp.network.model.GpsList

class NetWorkRepository {

    private val client = RetrofitInstance.getInstance().create(API::class.java)

    // 전체 위도 경도 API 호출
    suspend fun getApiTest() = client.getApiTest()

    // 범위 내 위도 경도 API 호출
    suspend fun getGPS(responseGps : GpsList) = client.getGPS(responseGps)

    // 이메일 중복 체크
    suspend fun validateDuplicateEmail(email : String) = client.validateDuplicateEmail(email)

    // 닉네임 중복 체크
    suspend fun validateDuplicateNick(nickname : String) = client.validateDuplicateNick(nickname)

    // 회원가입
    suspend fun signUp(signUp: SignUp) = client.signUp(signUp)
}