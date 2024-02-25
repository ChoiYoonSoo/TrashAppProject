package com.example.trashapp

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.trashapp.databinding.ActivityMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import java.security.MessageDigest
import net.daum.mf.map.api.MapView

class MapActivity : AppCompatActivity() {
    //private val TAG = "SOL_LOG"
    //@RequiresApi(Build.VERSION_CODES.P)
    private lateinit var binding : ActivityMapBinding   // 뷰 바인딩
    private lateinit var mapView : MapView              // 카카오 지도 뷰
    private var missionMapData = ArrayList<MissionMapData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mapView = binding.mapView   // 카카오 지도 뷰

        missionMapData.add(MissionMapData("강남구1",37.527126,127.044038))
        missionMapData.add(MissionMapData("강남구2",37.527701,127.040818))
        missionMapData.add(MissionMapData("강남구3",37.528211,127.039334))
        missionMapData.add(MissionMapData("강남구4",37.528804,127.037537))
        missionMapData.add(MissionMapData("강남구5",37.527534,127.028738))

        setMissionMark(missionMapData)


        // 서울시청에 마커 추가
        val marker = MapPOIItem()
        marker.apply {
            itemName = "강남구"   // 마커 이름
            mapPoint = MapPoint.mapPointWithGeoCoord(37.518954, 127.049961)   // 좌표
            markerType = MapPOIItem.MarkerType.CustomImage          // 마커 모양 (커스텀)
            customImageResourceId = R.drawable.custom_marker               // 커스텀 마커 이미지
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양 (커스텀)
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
            customSelectedImageResourceId = R.drawable.custom_marker     // 클릭 시 커스텀 마커 이미지
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
            mapView.setMapCenterPoint(mapPoint,true)
        }
        mapView.addPOIItem(marker)


//        val mapView = MapView(this)
//        val mapViewContainer = findViewById<ViewGroup>(R.id.map_view)
//        mapViewContainer.addView(mapView)

        // 해시값 얻기
//        try {
//            val information = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//            val signatures = information.signingInfo.apkContentsSigners
//            for (signature in signatures) {
//                val md = MessageDigest.getInstance("SHA").apply {
//                    update(signature.toByteArray())
//                }
//                val HASH_CODE = String(Base64.encode(md.digest(), 0))
//
//                Log.d(TAG, "HASH_CODE -> $HASH_CODE")
//            }
//        } catch (e: Exception) {
//            Log.d(TAG, "Exception -> $e")
//        }
    }

    private fun setMissionMark(dataList:ArrayList<MissionMapData>){
        for (data in dataList){
            var marker = MapPOIItem()
            marker.apply {
                itemName = data.missionName
                mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude,data.longitude)
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
            }
            mapView.addPOIItem(marker)
        }
    }

    data class MissionMapData(
        var missionName: String? = null, // 미션 이름
        var latitude : Double, // 위도
        var longitude : Double // 경도
    )
}