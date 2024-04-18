package com.example.trashapp.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.trashapp.view.SharedPreferencesManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        launchMainActivity()
    }

    private fun launchMainActivity(){
        val intent = Intent(this, MainActivity::class.java).apply {
            if (SharedPreferencesManager.getToken(this@SplashActivity) == null) {
                putExtra("fragmentToLoad", "introFragment")
            } else {
                val token = SharedPreferencesManager.getToken(this@SplashActivity).toString()
                putExtra("token", token)
                putExtra("fragmentToLoad", "mapFragment")
            }
        }
        startActivity(intent)
        finish()
    }
}