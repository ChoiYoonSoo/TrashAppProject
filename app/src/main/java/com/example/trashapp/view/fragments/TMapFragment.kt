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
import com.example.trashapp.databinding.FragmentTMapBinding
import com.example.trashapp.viewmodel.ApiListViewModel
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TMapFragment : Fragment() {

    private lateinit var tMapView: TMapView

    private lateinit var binding: FragmentTMapBinding

    private val viewModel: ApiListViewModel by activityViewModels()

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

        tMapView = TMapView(activity).apply {
            setSKTMapApiKey("aV91GWA72uaSSfI8rtqpda7n69nzB8OpKoO0Znse")
        }
        binding.tmapView.addView(tMapView)

        binding.tmapBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tMapView.setCenterPoint( 126.8820342335089, 37.47960336657709)

        setMarker(37.47960336657709, 126.8820342335089,"start")
        setMarker(viewModel.selectMapData!!.latitude, viewModel.selectMapData!!.longitude,"end")

        drawPedestrianPath()
    }

    // 마커 찍기
    fun setMarker(latitude: Double, longitude: Double, markerId: String){
        val markerItem = TMapMarkerItem()
        val tMapPoint = TMapPoint(latitude, longitude)
        val bitmap = getBitmapFromVectorDrawable(R.drawable.tmapmarker)

        markerItem.icon = bitmap // 마커 아이콘 지정
        markerItem.tMapPoint = tMapPoint // 마커의 좌표 지정
        markerItem.name = "marker" // 마커의 타이틀 지정
        tMapView.addMarkerItem(markerId, markerItem) // 지도에 마커 추가
    }

    // 경로 그리기
    private fun drawPedestrianPath() {
        // 출발지와 목적지 설정
        val startPoint = TMapPoint(37.47960336657709, 126.8820342335089)
        val endPoint = TMapPoint(viewModel.selectMapData!!.latitude, viewModel.selectMapData!!.longitude)

        // 보행자 경로 데이터 검색 및 표시
        lifecycleScope.launch(Dispatchers.IO){
            try {
                val pathData = TMapData()
                val polyLine = pathData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint)
                withContext(Dispatchers.Main) {
                    polyLine.lineColor = Color.RED
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