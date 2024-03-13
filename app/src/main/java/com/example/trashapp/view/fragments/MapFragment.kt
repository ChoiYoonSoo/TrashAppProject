package com.example.trashapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trashapp.R
import com.example.trashapp.data.MapData
import com.example.trashapp.databinding.FragmentIntroBinding
import com.example.trashapp.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {

    private lateinit var binding : FragmentMapBinding
    private lateinit var mapView : MapView              // 카카오 지도 뷰
    private var mapData = ArrayList<MapData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater,container,false)
        mapView = binding.mapView   // 카카오 지도 뷰

        // 위도 경도 테스트 하드 코딩
        mapData.add(MapData("쓰레기통1",37.577375,126.97216820000001))
//        mapData.add(MapData("쓰레기통2",37.527701,127.040818))
//        mapData.add(MapData("쓰레기통3",37.528211,127.039334))
//        mapData.add(MapData("쓰레기통4",37.528804,127.037537))
//        mapData.add(MapData("쓰레기통5",37.527534,127.028738))

        setMark(mapData)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setMark(dataList:ArrayList<MapData>){
        for (data in dataList){
            var marker = MapPOIItem()
            marker.apply {
                itemName = data.name
                mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude,data.longitude)
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
            }
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.577375,126.97216820000001),true)
            mapView.addPOIItem(marker)
        }
    }

}

//private val TAG = "SOL_LOG"
//@RequiresApi(Build.VERSION_CODES.P)

// 서울시청에 마커 추가
//        val marker = MapPOIItem()
//        marker.apply {
//            itemName = "강남구"   // 마커 이름
//            mapPoint = MapPoint.mapPointWithGeoCoord(37.518954, 127.049961)   // 좌표
//            markerType = MapPOIItem.MarkerType.CustomImage          // 마커 모양 (커스텀)
//            customImageResourceId = R.drawable.custom_marker               // 커스텀 마커 이미지
//            selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양 (커스텀)
//            selectedMarkerType = MapPOIItem.MarkerType.RedPin
//            customSelectedImageResourceId = R.drawable.custom_marker     // 클릭 시 커스텀 마커 이미지
//            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
//            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
//            mapView.setMapCenterPoint(mapPoint,true)
//        }
//        mapView.addPOIItem(marker)


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