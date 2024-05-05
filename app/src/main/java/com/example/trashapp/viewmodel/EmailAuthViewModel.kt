package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.network.model.EmailAuth
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class EmailAuthViewModel : ViewModel() {
    private val newWorkRepository = NetWorkRepository()

    var authNumber : String = ""

    private val _isEmailAuthSuccess = MutableLiveData<Boolean?>()
    var isEmailAuthSuccess : LiveData<Boolean?> = _isEmailAuthSuccess

    fun getEmailAuth(emailAuth: EmailAuth) = viewModelScope.launch {
        try {
            val result = newWorkRepository.getEmailAuth(emailAuth)
            Log.d("이메일 인증 통신 성공", result.toString())
            _isEmailAuthSuccess.postValue(true)
        } catch (e: Exception) {
            _isEmailAuthSuccess.postValue(false)
            Log.d("이메일 인증 통신 실패", e.toString())
        }
    }

    fun resetClear() {
        _isEmailAuthSuccess.value = null
    }

}