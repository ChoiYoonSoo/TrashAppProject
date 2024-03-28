package com.example.trashapp.repository

import com.example.trashapp.network.API
import com.example.trashapp.network.RetrofitInstance

class NetWorkRepository {

    private val client = RetrofitInstance.getInstance().create(API::class.java)

    // CampSite API 호출
    suspend fun getCampList() = client.getCampList(
        serviceKey = "CHdmmPOSZH0w4XZcQCYEu5hTg6xmEBkTdIVI1wIEwXyT3mog4I%2BsMMDUOzt2KQmjjpyp%2F6I%2FO39ruRSemF4vSw%3D%3D",
        numOfRows = 10,
        pageNo = 1,
        mobileOS = "ETC",
        mobileApp = "AppTest",
        type = "json"
    )

}