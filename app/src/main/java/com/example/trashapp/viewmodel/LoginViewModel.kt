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
    var token: String = ""

    private val _isLoginSuccess = MutableLiveData<Boolean?>()
    var isLoginSuccess : LiveData<Boolean?> = _isLoginSuccess

    private val _isPasswordSuccess = MutableLiveData<Boolean?>()
    var isPasswordSuccess: LiveData<Boolean?> = _isPasswordSuccess

    private val _isTokenSuccess = MutableLiveData<Boolean?>()
    val isTokenSuccess: LiveData<Boolean?> = _isTokenSuccess

    // 로그인
    fun login(login: Login) = viewModelScope.launch{
        try {
            val response = newWorkRepository.login(login)
            val newToken = response.headers()["Authorization"]
            if(newToken != null){
                token = newToken.toString()
                _isTokenSuccess.postValue(true)
                resetClear()
                Log.d("API 통신하여 받아온 토큰 값 : ", response.headers()["Authorization"].toString())
                Log.d("로그인 통신 ", "성공")
            }
            else{
                token = ""
                _isTokenSuccess.postValue(false)
                resetClear2()
            }

        }catch (e: Exception) {
            Log.e("로그인 통신 ", "실패")
            resetClear2()
            token = ""
            _isTokenSuccess.postValue(false)
        }
    }

    fun isLoginSuccess(value: Boolean) {
        _isLoginSuccess.postValue(value)
    }

    fun isPasswordSuccess(value: Boolean) {
        _isPasswordSuccess.postValue(value)
    }

    fun resetClear() {
        email = ""
        password = ""
        _isLoginSuccess.value = null
        _isPasswordSuccess.value = null
    }

    fun resetClear2(){
        _isLoginSuccess.value = null
        _isPasswordSuccess.value = null
    }

    fun tokenClear() {
        _isTokenSuccess.value = null
    }

}