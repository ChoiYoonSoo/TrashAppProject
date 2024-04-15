package com.example.trashapp.view.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.camera.video.Quality
import androidx.navigation.findNavController
import com.example.trashapp.R
import com.example.trashapp.databinding.ActivityMainBinding
import com.example.trashapp.view.SharedPreferencesManager
import com.example.trashapp.viewmodel.CurrentGpsViewModel
import com.example.trashapp.viewmodel.UserInfoViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val userInfoViewModel: UserInfoViewModel by viewModels()

    private val currentGpsViewModel : CurrentGpsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("token")?.let { token ->
            userInfoViewModel.token = token
        }

        val (latitude, longitude) = SharedPreferencesManager.getLocation(this)
        currentGpsViewModel.setGps(latitude, longitude)
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