package com.example.trashapp.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.example.trashapp.R
import com.example.trashapp.databinding.ActivityMainBinding
import com.example.trashapp.utils.RequestPermissionsUtil
import com.example.trashapp.viewmodel.CurrentGpsViewModel
import com.example.trashapp.viewmodel.UserInfoViewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val userInfoViewModel: UserInfoViewModel by viewModels()

    private val currentGpsViewModel: CurrentGpsViewModel by viewModels()

    private var permissionDialog: AlertDialog? = null

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
        } else {
            getLocation()
        }

        intent.getStringExtra("token")?.let { token ->
            userInfoViewModel.token = token
        }

    }

    // 위치 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    // 권한 거부에 대한 대응
                    explainPermissionRequirement()
                }
            }
        }
    }

    // 권한 요청 설명
    private fun explainPermissionRequirement() {
        // 사용자가 '다시 묻지 않음'을 선택하고 권한을 거부한 경우
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 권한 필요")
            .setMessage("이 앱은 정상적인 기능 수행을 위해 위치 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
            .setPositiveButton("설정") { dialog, which ->
                // 사용자가 설정을 누르면 앱의 설정 화면으로 이동
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                finish()
            }
            .setNegativeButton("취소") { dialog, which ->
                // 사용자가 취소를 누르면 앱 종료
                finish()
            }
            .setCancelable(false)
        permissionDialog = builder.create()
        permissionDialog?.show()
    }

    // 위치 업데이트 시작
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        Log.d("위치 업데이트@@@@@@@","실행")
        permissionDialog?.dismiss()
        val locationRequest = LocationRequest.Builder(3000L) // 업데이트 간격을 3초로 설정
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY) // 높은 정확도 요구
            .setMinUpdateIntervalMillis(2000L) // 업데이트 간격 최소 2초
            .build()

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // 위치 업데이트 중지
    fun stopLocationUpdates() {
        Log.d("위치 업데이트@@@@@@@","중지")
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    // 현재 위치 가져오기
    @SuppressLint("MissingPermission")
    fun getLocation() {
        Log.d("현재위치 가져오기@@@@","실행")
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
//                    currentGpsViewModel.currentGps(36.8330149230957, 127.17927825927734)
                    currentGpsViewModel.currentGps(location.latitude, location.longitude)
                }
            }
            .addOnFailureListener { fail ->
                Log.d("---GPS---","getLocation() 실패")
            }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (location in p0.locations) {
                // 위치 정보 업데이트
                currentGpsViewModel.setGps(location.latitude, location.longitude)
            }
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
            if (p0?.isLocationAvailable == false) {
                Log.d("---GPS---", "위치 정보 사용 불가")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        when (intent.getStringExtra("fragmentToLoad")) {
            "mapFragment" -> {
                val navController = findNavController(R.id.fragmentContainerView)
                if (navController.currentDestination?.id != R.id.mapFragment) {
                    binding.fragmentContainerView.findNavController().navigate(R.id.action_introFragment_to_mapFragment)
                }
            }
        }
    }

    // 빈 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            initialTouchX = ev.x
            initialTouchY = ev.y
        }

        if (ev?.action == MotionEvent.ACTION_UP) {
            val deltaX = Math.abs(ev.x - initialTouchX)
            val deltaY = Math.abs(ev.y - initialTouchY)

            // 드래그가 아니라고 판단되는 경우에만 키보드를 내림
            // 여기서 10은 드래그로 간주하기 전에 허용되는 최소 이동 거리
            if (deltaX < 10 && deltaY < 10) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                currentFocus?.let {
                    if (it is EditText) {
                        it.clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private var initialTouchX = 0f
    private var initialTouchY = 0f
}