package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.trashapp.repository.UserTokenRepository

class UserTokenViewModel(private val userRepository: UserTokenRepository) : ViewModel(){

    fun saveToken(token: String) {
        userRepository.saveToken(token)
        Log.d("UserTokenViewModel에 저장된 토큰 값 : ", token)
    }

    fun getToken(): String? {
        return userRepository.getToken()
    }

    fun removeToken() {
        userRepository.removeToken()
    }

}