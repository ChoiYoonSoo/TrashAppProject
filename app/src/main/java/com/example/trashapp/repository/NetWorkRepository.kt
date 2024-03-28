package com.example.trashapp.repository

import com.example.trashapp.network.API
import com.example.trashapp.network.RetrofitInstance

class NetWorkRepository {

    private val client = RetrofitInstance.getInstance().create(API::class.java)

    // 위도 경도 API 호출
    suspend fun getApiTest() = client.getApiTest(
        url = "https://api.odcloud.kr/api/15124273/v1/uddi:57420117-a24a-4b95-bf71-987821377346?page=1&perPage=10&serviceKey=CHdmmPOSZH0w4XZcQCYEu5hTg6xmEBkTdIVI1wIEwXyT3mog4I%2BsMMDUOzt2KQmjjpyp%2F6I%2FO39ruRSemF4vSw%3D%3D",
        page = 1,
        perPage = 10,
        serviceKey = "CHdmmPOSZH0w4XZcQCYEu5hTg6xmEBkTdIVI1wIEwXyT3mog4I%2BsMMDUOzt2KQmjjpyp%2F6I%2FO39ruRSemF4vSw%3D%3D",
    )

}