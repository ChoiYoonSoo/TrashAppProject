package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.network.model.EmailAuth
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class EmailAuthViewModel : ViewModel() {
    private val newWorkRepository = NetWorkRepository()

    var authNumber : String = ""

    fun getEmailAuth(emailAuth: EmailAuth) = viewModelScope.launch {
        try {
            val result = newWorkRepository.getEmailAuth(emailAuth)
            Log.d("이메일 인증 통신 성공", result.toString())
        } catch (e: Exception) {
            Log.d("이메일 인증 통신 실패", e.toString())
        }
    }

}