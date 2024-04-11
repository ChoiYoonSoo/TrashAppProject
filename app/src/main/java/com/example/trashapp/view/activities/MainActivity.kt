package com.example.trashapp.view.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.trashapp.R
import com.example.trashapp.databinding.ActivityMainBinding
import com.example.trashapp.view.fragments.IntroFragment
import com.example.trashapp.view.fragments.MapFragment
import com.example.trashapp.viewmodel.UserInfoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val userInfoViewModel: UserInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("token")?.let { token ->
            userInfoViewModel.token = token
        }

//        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val isGpsEnabled : Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val isNetworkEnabled : Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//
//        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this@MainActivity,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
//        }
//        else{
//            when{
//                isNetworkEnabled ->{
//                    val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                    var getLongitude = location?.longitude!!
//                    var getLatitude = location.latitude
//                }
//                isGpsEnabled -> {
//                    val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                    var getLongitude = location?.longitude!!
//                    var getLatitude = location.latitude
//                }
//                else ->{
//
//                }
//            }
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1F, gpsLocationListener)
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1F, gpsLocationListener)
//            lm.removeUpdates(gpsLocationListener)
//        }
//        lm.removeUpdates(gpsLocationListener)
//    }
//
//    val gpsLocationListener = object : LocationListener{
//        override fun onLocationChanged(location: Location) {
//            val provider: String = location.provider!!
//            val longitude: Double = location.longitude
//            val latitude: Double = location.latitude
//            val altitude: Double = location.altitude
//
//            Log.d("Main", "위치정보 - $provider, $longitude, $latitude, $altitude")
//        }
//        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
//        }
//        override fun onProviderEnabled(provider: String) {
//        }
//        override fun onProviderDisabled(provider: String) {
//        }
//    }
    }

    override fun onResume() {
        super.onResume()
        when(intent.getStringExtra("fragmentToLoad")){
            "mapFragment" -> {
                binding.fragmentContainerView.findNavController().navigate(R.id.action_introFragment_to_mapFragment)
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
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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