package com.example.trashapp.repository

import android.content.Context
import com.example.trashapp.view.SharedPreferencesManager

class UserTokenRepository(private val context: Context) {
    fun saveToken(token: String) {
        SharedPreferencesManager.saveToken(context, token)
    }

    fun getToken(): String? {
        return SharedPreferencesManager.getToken(context)
    }

    fun removeToken() {
        SharedPreferencesManager.removeToken(context)
    }
}
