package com.example.trashapp.view.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trashapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}