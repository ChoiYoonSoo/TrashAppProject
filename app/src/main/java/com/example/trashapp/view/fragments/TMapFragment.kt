package com.example.trashapp.view.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.trashapp.R
import com.example.trashapp.data.TmapApiRequest
import com.example.trashapp.databinding.FragmentTMapBinding
import com.example.trashapp.view.activities.MainActivity
import com.example.trashapp.viewmodel.ApiListViewModel
import com.example.trashapp.viewmodel.CurrentGpsViewModel
import com.skt.Tmap.TMapCircle
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint

class TMapFragment : Fragment() {

    private lateinit var tMapView: TMapView

    private lateinit var binding: FragmentTMapBinding

    private val viewModel: ApiListViewModel by activityViewModels()

    private val currentGpsViewModel: CurrentGpsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.startLocationUpdates()

        tMapView = TMapView(activity).apply {
            setSKTMapApiKey("aV91GWA72uaSSfI8rtqpda7n69nzB8OpKoO0Znse")
        }
        binding.tmapView.addView(tMapView)
        Log.d("지도", "ok")

        // 뒤로가기 버튼 클릭 시
        binding.tmapBackBtn.setOnClickListener {
            (activity as? MainActivity)?.stopLocationUpdates()
            parentFragmentManager.popBackStack()
        }

        // 실시간 위치 업데이트
        currentGpsViewModel.lastLocation.observe(viewLifecycleOwner) { location ->
            Log.d("티맵 실시간 위치 업데이트", "${location.latitude}, ${location.longitude}")

            tMapView.removeMarkerItem("current")

            setMarker(location.latitude, location.longitude, "current")
        }

        // 지도에 마커 찍기
        tMapView.postDelayed({
            currentGpsViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
                    tMapView.setCenterPoint(location.longitude, location.latitude)
                    setMarker(location.latitude + 0.0001, location.longitude, "start")
                setMarker(location.latitude, location.longitude, "current")
                    setMarker(
                        viewModel.selectMapData!!.latitude,
                        viewModel.selectMapData!!.longitude,
                        "end"
                    )
                    drawPedestrianPath(location.latitude, location.longitude)
                    val tmapApi = TmapApiRequest(
                        location.longitude,
                        location.latitude,
                        viewModel.selectMapData!!.longitude,
                        viewModel.selectMapData!!.latitude,
                        "출발지",
                        "도착지",
                        10
                    )
                    viewModel.getTmapApi(tmapApi)
                }

        }, 1000)

        // 출발지와 도착지 거리 구하기
        viewModel.totalDistance.observe(viewLifecycleOwner) { distance ->
            val distanceText = if (distance < 1000) {
                "${distance}m"
            }else{
                val distanceInKilometers = distance / 1000.0
                "%.1f km".format(distanceInKilometers)
            }
            binding.tmapText3.text = distanceText
            binding.tmapCardView.visibility = View.VISIBLE
        }

        // 출발지와 도착지 소요시간 구하기
        viewModel.totalTime.observe(viewLifecycleOwner) { time ->
            val timeText = if (time < 60) {
                "1분 미만"
            }else{
                val timeInMinutes = time / 60
                val hours = timeInMinutes / 60
                val minutes = timeInMinutes % 60
                if (hours > 0) {
                    "${hours}시간 ${minutes}분"
                } else {
                    "${timeInMinutes}분"
                }
            }
            binding.tmapText2.text = timeText
            binding.tmapCardView.visibility = View.VISIBLE
        }

    }

    // 마커 찍기
    fun setMarker(latitude: Double, longitude: Double, markerId: String) {
        Log.d("마커", "ok")
        val markerItem = TMapMarkerItem()
        val tMapPoint = TMapPoint(latitude, longitude)

        // 마커 아이콘 설정
        when (markerId) {
            "start" -> markerItem.icon = getBitmapFromVectorDrawable(R.drawable.startmarker)
            "end" -> markerItem.icon = getBitmapFromVectorDrawable(R.drawable.endmarker)
            "current" -> markerItem.icon = getBitmapFromVectorDrawable(R.drawable.current_gps)
        }

        markerItem.tMapPoint = tMapPoint // 마커의 좌표 지정
        markerItem.name = "marker" // 마커의 타이틀 지정
        tMapView.addMarkerItem(markerId, markerItem) // 지도에 마커 추가

    }

    // 경로 그리기
    private fun drawPedestrianPath(lat: Double, lon: Double) {
        Log.d("경로", "ok")
        // 출발지와 목적지 설정
        val startPoint = TMapPoint(lat, lon)
        val endPoint =
            TMapPoint(viewModel.selectMapData!!.latitude, viewModel.selectMapData!!.longitude)

        // 보행자 경로 데이터 검색 및 표시
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val pathData = TMapData()
                val polyLine = pathData.findPathDataWithType(
                    TMapData.TMapPathType.PEDESTRIAN_PATH,
                    startPoint,
                    endPoint
                )
                withContext(Dispatchers.Main) {
                    polyLine.lineColor = Color.rgb(75, 121, 225)
                    polyLine.lineWidth = 15f
                    tMapView.addTMapPolyLine("pedestrianPath", polyLine)
                    tMapView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 마커 커스텀 이미지 Vector -> Bitmap 변환 함수
    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = context?.let { ContextCompat.getDrawable(it, drawableId) }
        if (drawable is VectorDrawable) {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        return null
    }
}