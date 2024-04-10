package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class UserInfoViewModel : ViewModel() {
    private val netWorkRepository = NetWorkRepository()

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> = _email

    private val _nickname = MutableLiveData<String?>()
    val nickname : LiveData<String?> = _nickname

    private val _profileImgPath = MutableLiveData<String?>()
    var profileImgPath : LiveData<String?> = _profileImgPath

    fun getUserInfo(token: String) = viewModelScope.launch {
        try{
            val user = netWorkRepository.getUserdata(token)
            Log.d("이메일", user.email)
            Log.d("닉네임", user.nickname)
            Log.d("프로필 이미지", user.profileImgPath.toString())

            _email.postValue(user.email)
            _nickname.postValue(user.nickname)

            if(user.profileImgPath == null){
                _profileImgPath.postValue("")
            }
            else{
                _profileImgPath.postValue(user.profileImgPath)
            }

        }catch (e: Exception){
            Log.e("유저 정보 가져오기 실패", "Error: ${e.message}")
        }
    }
}