package com.example.trashapp.network

import com.example.trashapp.data.ApiValue
import com.example.trashapp.data.Login
import com.example.trashapp.data.SignUp
import com.example.trashapp.network.model.GpsList
import com.example.trashapp.network.model.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
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

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ) : Response<ResponseBody>

    @POST("getUserdata")
    suspend fun getUserdata(@Header("Authorization") accessToken : String) : User
}