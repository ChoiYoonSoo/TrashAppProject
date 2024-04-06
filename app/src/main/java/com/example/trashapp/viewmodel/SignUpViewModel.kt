package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.SignUp
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch
import kotlin.math.sign

class SignUpViewModel : ViewModel() {
    private val newWorkRepository = NetWorkRepository()

    var email: String = ""
    var password: String = ""
    var nickname: String = ""

    private val _isDuplicateEmail = MutableLiveData<Boolean>()
    var isDuplicateEmail : LiveData<Boolean> = _isDuplicateEmail

    private val _isDuplicateNick = MutableLiveData<Boolean>()
    var isDuplicateNick : LiveData<Boolean> = _isDuplicateNick

    private val _isValidatePassword = MutableLiveData<Boolean>()
    var isValidatePassword : LiveData<Boolean> = _isValidatePassword

    fun duplicateEmailCheck(email: String) = viewModelScope.launch {
        try{
            val response = newWorkRepository.validateDuplicateEmail(email)
            _isDuplicateEmail.value = true
            Log.d("이메일 통신 성공", "response : $response")
        }catch (e: Exception) {
            _isDuplicateEmail.value = false
            Log.e("이메일 통신 실패", "Error fetching campsite list", e)
        }
    }

    fun duplicateNickCheck(nickname: String) = viewModelScope.launch {
        try{
            val response = newWorkRepository.validateDuplicateNick(nickname)
            _isDuplicateNick.value = true
            Log.d("닉네임 통신 성공", "response : $response")
        }catch (e: Exception) {
            _isDuplicateNick.value = false
            Log.e("닉네임 통신 실패", "Error fetching campsite list", e)
        }
    }

    fun validatePassword(password: String) {
        val pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()-_+=])[A-Za-z\\d!@#\$%^&*()-_+=]{8,}\$".toRegex()

        if(password.matches(pattern)) {
            _isValidatePassword.value = true
            Log.d("패스워드 유효성검사 성공", "!")
        } else {
            _isValidatePassword.value = false
            Log.d("패스워드 유효성 검사 실패", "비비밀번호는 8자리 이상이어야 합니다.")
        }
    }

    fun signUp(signUp: SignUp) = viewModelScope.launch {
        try{
            val response = newWorkRepository.signUp(signUp)
            Log.d("회원가입 통신 성공", "response : $response")
        }catch (e: Exception) {
            Log.e("회원가입 통신 실패", "Error fetching campsite list", e)
        }
    }
}