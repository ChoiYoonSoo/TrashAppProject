package com.example.trashapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// http://cgt.iptime.org:8080/ 경태 집 공유기
// http://203.230.194.162:8080/ 경태 외부 ip
// http://ohs0407.iptime.org:8080 현식 집 공유기
// http://192.168.194.205:8080/ 학교 내부 ip
// http://10.0.2.2:8080/ 가상 디바이스
// http://192.168.0.16:8080/ 집 내부 ip
object RetrofitInstance {
    private const val BASE_URL = "http://192.168.0.16:8080/"

    private val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val kakaoClient =  Retrofit
        .Builder()
        .baseUrl("https://dapi.kakao.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance(): Retrofit {
        return client
    }
    fun getKakaoInstance(): Retrofit {
        return kakaoClient
    }

}