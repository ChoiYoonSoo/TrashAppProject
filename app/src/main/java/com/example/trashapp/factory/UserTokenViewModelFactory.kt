package com.example.trashapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trashapp.repository.UserTokenRepository
import com.example.trashapp.viewmodel.UserTokenViewModel

class UserTokenViewModelFactory(private val userRepository: UserTokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserTokenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserTokenViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
