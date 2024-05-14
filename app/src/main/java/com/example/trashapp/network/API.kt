package com.example.trashapp.network

import com.example.trashapp.data.ApiValue
import com.example.trashapp.data.SignUp
import com.example.trashapp.data.TmapApiRequest
import com.example.trashapp.network.model.CancelReport
import com.example.trashapp.network.model.EmailAuth
import com.example.trashapp.network.model.GpsList
import com.example.trashapp.network.model.ModifyTrashcan
import com.example.trashapp.network.model.MyReportList
import com.example.trashapp.network.model.ReportTrashCan
import com.example.trashapp.network.model.ResultSearchKeyword
import com.example.trashapp.network.model.TmapApi
import com.example.trashapp.network.model.UnknownTrashcan
import com.example.trashapp.network.model.User
import com.example.trashapp.network.model.UserReportList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


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

    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Header("Authorization") key: String,
        @Query("query") query: String,
    ) : ResultSearchKeyword

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "appKey: aV91GWA72uaSSfI8rtqpda7n69nzB8OpKoO0Znse"
    )
    @POST("routes/pedestrian?version=1")
    suspend fun getTmapApi(
        @Body requestBody: TmapApiRequest
    ) : TmapApi

    @POST("mailAuth")
    suspend fun getEmailAuth(@Body emailAuth: EmailAuth)

    @Multipart
    @POST("registerTrashcan")
    suspend fun newTrashcan(
        @Header("Authorization") token: String,
        @Part("RegisterTrashcanDTO") location: RequestBody,
        @Part image: MultipartBody.Part
    ) : Response<String>
    
    @POST("findReportCount")
    suspend fun findReportCount(@Body trashcanId: Long) : Int

    @POST("reportTrashcan")
    suspend fun reportTrashcan(@Body reportTrashcan : ReportTrashCan, @Header("Authorization") token : String) : Response<String>

    @GET("findAllUnknownTrashcans")
    suspend fun findAllUnknownTrashcans() : List<UnknownTrashcan>

    @POST("deleteUnknownTrashcan")
    suspend fun deleteUnknownTrashcan(@Body trashcanId: Long)

    @GET("findAllReportTrashcan")
    suspend fun findAllReportTrashcan() : List<UserReportList>

    @POST("deleteTrashcan")
    suspend fun deleteTrashcan(@Body reportTrashCan: ReportTrashCan)

    @POST("modifyTrashcan")
    suspend fun modifyTrashcan(@Body modifyTrashcan: ModifyTrashcan)

    @POST("myReportTrashcans")
    suspend fun myReportTrashcans(@Header("Authorization") token : String) : List<MyReportList>

    @POST("deleteReportTrashcan")
    suspend fun deleteReportTrashcan(@Body reportTrashcan: ReportTrashCan, @Header("Authorization") token : String) : Response<String>

    @Multipart
    @POST("detect")
    suspend fun imageYolo(@Part image: MultipartBody.Part)

    @POST("cancelReport")
    suspend fun cancelReport(@Body cancelReport: CancelReport)
}