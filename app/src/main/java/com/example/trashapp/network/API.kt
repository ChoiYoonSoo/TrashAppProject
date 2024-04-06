package com.example.trashapp.network

import com.example.trashapp.data.ApiValue
import com.example.trashapp.data.SignUp
import com.example.trashapp.network.model.GpsList
import com.google.android.gms.common.api.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface API {
    @GET("findTrashcans")
    suspend fun getApiTest(): List<ApiValue>

    @POST("getGPS")
    suspend fun getGPS(@Body responseGps: GpsList) : List<ApiValue>

    @POST("validateDuplicateUser")
    suspend fun validateDuplicateEmail(@Body email: String)

    @POST("validateDuplicateNickname")
    suspend fun validateDuplicateNick(@Body nickname: String)

    @POST("responseUserInfo")
    suspend fun signUp(@Body signUp: SignUp)
}