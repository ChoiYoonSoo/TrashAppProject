package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
    private var selectedMarker : MapPOIItem? = null
    private var lastY : Float = 0.0f

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
        setupDraggableBottomPanel()


    }

    // 스크롤 이동
    @SuppressLint("ClickableViewAccessibility")
    private fun setupDraggableBottomPanel() {
        val draggableLayout = binding.root.findViewById<ConstraintLayout>(R.id.test) // yourDraggableLayoutId는 드래그 가능한 레이아웃의 ID로 교체해야 함.
        draggableLayout.setOnTouchListener { view, event ->
            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.rawY - lastY
                    val newHeight = (layoutParams.height + deltaY).toInt()
                    layoutParams.height = newHeight.coerceAtLeast(300) // 여기서 300은 최소 높이, 필요에 따라 조정 가능
                    view.layoutParams = layoutParams
                    lastY = event.rawY
                    true
                }
                else -> false
            }
        }
    }

    // 지도 마커 띄우기
    private fun setMark(dataList:ArrayList<MapData>){
        for (data in dataList){
            var marker = MapPOIItem()
            marker.apply {
                itemName = data.name
                mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude,data.longitude)
                customImageBitmap = getBitmapFromVectorDrawable(R.drawable.bin_marker)
                markerType = MapPOIItem.MarkerType.CustomImage
            }
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.577375,126.97216820000001),true)
            mapView.addPOIItem(marker)

        }

        // 마커 클릭 이벤트 리스너 설정
        mapView.setPOIItemEventListener(object : MapView.POIItemEventListener {

            override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
                // 마커 클릭 시 동작
                // 여기에 커스텀 뷰를 표시하는 로직을 구현
                // 예: 커스텀 다이얼로그 띄우기, 다른 액티비티 또는 프래그먼트로 넘어가기 등
                val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.test)
                val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
                // 뷰에 애니메이션 적용
                binInfoLayout.startAnimation(slideUpAnimation)
                binInfoLayout.visibility = View.VISIBLE // 여기를
            }

            override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
                // 기본 말풍선 클릭 시 동작 (이 경우 사용하지 않음)
            }

            override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
                // 말풍선의 버튼 클릭 시 동작 (이 경우 사용하지 않음)
            }

            override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
                // 마커 드래그 시 동작 (이 경우 사용하지 않음)
            }
        })
    }

    // Vector -> Bitmap 변환
    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = context?.let { ContextCompat.getDrawable(it, drawableId) }
        if (drawable is VectorDrawable) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
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
