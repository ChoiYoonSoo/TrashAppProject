package com.example.trashapp.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import android.Manifest

class RequestPermissionsUtil(private val context: Context) {

    private val REQUEST_LOCATION = 1

    /** 위치 권한 SDK 버전 29 이상**/
    @RequiresApi(Build.VERSION_CODES.Q)
    private val permissionsLocationUpApi29Impl = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    /** 위치 권한 SDK 버전 29 이하**/
    @TargetApi(Build.VERSION_CODES.P)
    private val permissionsLocationDownApi29Impl = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /** 위치정보 권한 요청**/
    fun requestLocation() {
        val allPermissions = if (Build.VERSION.SDK_INT >= 29) permissionsLocationUpApi29Impl else permissionsLocationDownApi29Impl
        if (allPermissions.any { ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(context as Activity, allPermissions, REQUEST_LOCATION)
        }
    }
}