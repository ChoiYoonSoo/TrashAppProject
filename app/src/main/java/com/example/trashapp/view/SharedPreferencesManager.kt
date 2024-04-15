package com.example.trashapp.view

import android.content.Context
import android.util.Log

object SharedPreferencesManager {
    private const val PREF_NAME = "user"
    private const val TOKEN_KEY = "token"
    private const val LOCATION = "location"

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

    fun saveLocation(context: Context, latitude: Double, longitude: Double) {
        val sharedPreferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putLong("latitude", java.lang.Double.doubleToRawLongBits(latitude))
            putLong("longitude", java.lang.Double.doubleToRawLongBits(longitude))
            apply()
        }
    }

    fun getLocation(context: Context): Pair<Double, Double> {
        val sharedPreferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE)
        val latitude = java.lang.Double.longBitsToDouble(sharedPreferences.getLong("latitude", java.lang.Double.doubleToRawLongBits(0.0)))
        val longitude = java.lang.Double.longBitsToDouble(sharedPreferences.getLong("longitude", java.lang.Double.doubleToRawLongBits(0.0)))
        return Pair(latitude, longitude)
    }
}
