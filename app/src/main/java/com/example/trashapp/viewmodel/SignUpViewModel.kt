package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.SignUp
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

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

    // 이메일 중복 체크
    fun duplicateEmailCheck(email: String) = viewModelScope.launch {
        try{
            val response = newWorkRepository.validateDuplicateEmail(email)
            _isDuplicateEmail.value = true
            Log.d("이메일 통신 ", "성공")
        }catch (e: Exception) {
            _isDuplicateEmail.value = false
            Log.e("이메일 통신 ", "실패")
        }
    }

    // 닉네임 중복 체크
    fun duplicateNickCheck(nickname: String) = viewModelScope.launch {
        try{
            val response = newWorkRepository.validateDuplicateNick(nickname)
            _isDuplicateNick.value = true
            Log.d("닉네임 통신 ", "성공")
        }catch (e: Exception) {
            _isDuplicateNick.value = false
            Log.e("닉네임 통신 ", "실패")
        }
    }

    // 비밀번호 유효성 검사
    fun validatePassword(password: String) {
        val pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()-_+=])[A-Za-z\\d!@#\$%^&*()-_+=]{8,}\$".toRegex()

        if(password.matches(pattern)) {
            _isValidatePassword.value = true
            Log.d("비밀 번호 유효성 검사 ", "성공")
        } else {
            _isValidatePassword.value = false
            Log.d("비밀 번호 유효성 검사 ", "실패")
        }
    }

    // 회원가입
    fun signUp(signUp: SignUp) = viewModelScope.launch {
        try{
            val response = newWorkRepository.signUp(signUp)
            Log.d("회원 가입 통신 ", "성공")
        }catch (e: Exception) {
            Log.e("회원 가입 통신 ", "실패")
        }
    }
}