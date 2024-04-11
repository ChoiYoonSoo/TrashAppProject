package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.Login
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){
    private val newWorkRepository = NetWorkRepository()

    var email: String = ""
    var password: String = ""
    var isLoginSeccess = false

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> = _token

    // 로그인
    fun login(login: Login) = viewModelScope.launch{
        try {
            val response = newWorkRepository.login(login)
            val newToken = response.headers()["Authorization"]
            _token.postValue(newToken)
            Log.d("API 통신하여 받아온 토큰 값 : ", response.headers()["Authorization"].toString())
            Log.d("로그인 통신 ", "성공")

        }catch (e: Exception) {
            Log.e("로그인 통신 ", "실패")
            _token.postValue(null)
        }
    }

}