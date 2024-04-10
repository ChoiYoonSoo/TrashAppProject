package com.example.trashapp.view

import android.content.Context
import android.util.Log

object SharedPreferencesManager {
    private const val PREF_NAME = "user"
    private const val TOKEN_KEY = "token"

    fun saveToken(context: Context, token: String) {
        Log.d("SharedPreference saveToken : ", token)
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        Log.d("SharedPreference getToken : ", sharedPreferences.getString(TOKEN_KEY, null).toString())
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun removeToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        //sharedPreferences.edit().putString(TOKEN_KEY, null).apply()
        sharedPreferences.edit().remove(TOKEN_KEY).apply() // 토큰 삭제
    }
}
