package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.network.model.TrashcanLocation
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CameraViewModel : ViewModel() {

    private val newWorkRepository = NetWorkRepository()

    var token : String? = null
    var category : String? = null
    var image : MultipartBody.Part? = null
    var currentLatitude : Double? = null
    var currentLongitude : Double? = null
    var location : TrashcanLocation? = null
    var addressEditText = ""
    private val _isSuccess : MutableLiveData<Boolean?> = MutableLiveData()
    var isSuccess : LiveData<Boolean?> = _isSuccess

    private val _isYoloSuccess : MutableLiveData<Boolean?> = MutableLiveData()
    var isYoloSuccess : LiveData<Boolean?> = _isYoloSuccess

    var newTrashcanError = ""

    // 사용자 쓰레기통 등록 요청
    fun newTrashcan(token : String, location : RequestBody, image : MultipartBody.Part) = viewModelScope.launch {
        try {
            Log.d("이미지 통신 newTrashcan", "토큰 : ${token}, 위치 : ${location}, 이미지 : ${image}")
            val response = newWorkRepository.newTrashcan(token, location, image)

            if(response.isSuccessful){
                newTrashcanError = response.body().toString()
                Log.d("쓰레기통 자동 등록 성공", newTrashcanError)
            }
            else{
                newTrashcanError = response.errorBody()!!.string()
                Log.d("쓰레기통 자동 등록 실패", newTrashcanError)
            }

            _isSuccess.postValue(true)
        }catch (e: Exception) {
           Log.d("이미지 서버 통신 실패", e.toString())
            _isSuccess.postValue(false)
        }
    }

    // 사진 욜로 판별
    fun imageYolo(image : MultipartBody.Part) = viewModelScope.launch {
        try{
            val response = newWorkRepository.imageYolo(image)
            Log.d("이미지 욜로 통신 완료", response.toString())
            _isYoloSuccess.postValue(true)
        }catch (e: Exception) {
            _isYoloSuccess.postValue(false)
            Log.d("이미지 욜로 통신 실패", e.toString())
        }
    }

    fun resetIsSuccess() {
        _isSuccess.value = null
    }

    fun resetIsYoloSuccess() {
        _isYoloSuccess.value = null
    }
}