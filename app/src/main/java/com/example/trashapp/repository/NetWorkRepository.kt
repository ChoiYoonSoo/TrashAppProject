package com.example.trashapp.repository

import com.example.trashapp.data.Login
import com.example.trashapp.data.SignUp
import com.example.trashapp.data.TmapApiRequest
import com.example.trashapp.network.API
import com.example.trashapp.network.RetrofitInstance
import com.example.trashapp.network.model.CancelReport
import com.example.trashapp.network.model.EmailAuth
import com.example.trashapp.network.model.GpsList
import com.example.trashapp.network.model.ModifyTrashcan
import com.example.trashapp.network.model.ReportTrashCan
import com.example.trashapp.network.model.TrashcanLocation
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart

class NetWorkRepository {

    private val client = RetrofitInstance.getInstance().create(API::class.java)

    private val kakaoClient = RetrofitInstance.getKakaoInstance().create(API::class.java)

    private val tmapClient = RetrofitInstance.getTmapInstance().create(API::class.java)

    private val yoloClient = RetrofitInstance.getYoloInstance().create(API::class.java)

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

    // 로그인
    suspend fun login(login : Login) = client.login(login.username, login.password)

    // 유저 정보 가져오기
    suspend fun getUserdata(accessToken : String) = client.getUserdata(accessToken)

    // 카카오 키워드 검색
    suspend fun searchKeyword(key : String, query : String) = kakaoClient.searchKeyword(key, query)

    // Tmap API 호출
    suspend fun getTmapApi(requestBody: TmapApiRequest) = tmapClient.getTmapApi(requestBody)

    // 이메일 인증
    suspend fun getEmailAuth(emailAuth: EmailAuth) = client.getEmailAuth(emailAuth)

    // 쓰레기통 등록
    suspend fun newTrashcan(token: String, location: RequestBody, image: MultipartBody.Part) = client.newTrashcan(token, location, image)

    // 쓰레기통 신고 횟수
    suspend fun findReportCount(trashcanId: Long) = client.findReportCount(trashcanId)

    // 쓰레기통 신고
    suspend fun reportTrashcan(reportTrashcan : ReportTrashCan, token : String) = client.reportTrashcan(reportTrashcan, token)

    // 사용자 쓰레기통 등록 조회
    suspend fun findAllUnknownTrashcans() = client.findAllUnknownTrashcans()

    // 사용자 쓰레기통 등록 삭제
    suspend fun deleteUnknownTrashcan(trashcanId: Long) = client.deleteUnknownTrashcan(trashcanId)

    // 사용자 쓰레기통 신고 조회
    suspend fun findAllReportTrashcan() = client.findAllReportTrashcan()

    // 쓰레기통 삭제
    suspend fun deleteTrashcan(reportTrashCan: ReportTrashCan) = client.deleteTrashcan(reportTrashCan)

    // 쓰레기통 수정
    suspend fun modifyTrashcan(modifyTrashcan: ModifyTrashcan) = client.modifyTrashcan(modifyTrashcan)

    // 나의 쓰레기통 신고 내역
    suspend fun myReportTrashcans(token: String) = client.myReportTrashcans(token)

    // 나의 쓰레기통 신고 내역 삭제
    suspend fun deleteReportTrashcan(reportTrashcan: ReportTrashCan, token: String) = client.deleteReportTrashcan(reportTrashcan, token)

    // 사진 욜로 판별
    suspend fun imageYolo(image : MultipartBody.Part) = yoloClient.imageYolo(image)

    // 사용자 신고 내역 취소
    suspend fun cancelReport(cancelReport: CancelReport) = client.cancelReport(cancelReport)
}