package com.example.trashapp.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.trashapp.utils.RequestPermissionsUtil
import com.example.trashapp.view.SharedPreferencesManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
        } else {
            getLocation()
        }
    }
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 승인되었을 때
                    Log.d("권한","승인")
                    getLocation()
                } else {
                    // 권한이 거부되었을 때
                    Log.d("권한","거부")
                    Toast.makeText(this, "위치 권한이 필요합니다. 설정에서 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun launchMainActivity(latitude: Double, longitude: Double){
        SharedPreferencesManager.saveLocation(this@SplashActivity, latitude, longitude)

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
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    Log.d("@@@@@ GPS @@@@@", "${location.latitude}, ${location.longitude}")
                    launchMainActivity(location.latitude, location.longitude)
                }
            }
            .addOnFailureListener { fail ->
                Log.d("@@@@@ GPS @@@@@","실패")
                launchMainActivity(37.5664056,126.9778222)
            }
    }
}