package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.trashapp.R
import com.example.trashapp.data.MapData
import com.example.trashapp.databinding.FragmentIntroBinding
import com.example.trashapp.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment(), MapView.MapViewEventListener, MapView.POIItemEventListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView              // 카카오 지도 뷰
    private var mapData = ArrayList<MapData>()
    private var lastY: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView   // 카카오 지도 뷰

        // 위도 경도 테스트 하드 코딩
        mapData.add(MapData("쓰레기통1", 37.577375, 126.97216820000001))
        mapData.add(MapData("쓰레기통2", 37.527380, 126.962169))
        mapData.add(MapData("쓰레기통3", 37.528211, 127.039334))
        mapData.add(MapData("쓰레기통4", 37.528804, 127.037537))
        mapData.add(MapData("쓰레기통5", 37.527534, 127.028738))

        setMark(mapData)
        setupDraggableBottomPanel()
        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)

    }

    // 지도 마커 띄우기
    private fun setMark(dataList: ArrayList<MapData>) {
        for (data in dataList) {
            var marker = MapPOIItem()
            marker.apply {
                itemName = data.name
                mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
                customImageBitmap = getBitmapFromVectorDrawable(R.drawable.bin_marker)
                markerType = MapPOIItem.MarkerType.CustomImage
            }
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.528211, 127.039334), true)
            mapView.addPOIItem(marker)
        }

    }

    // 스크롤 이동
    @SuppressLint("ClickableViewAccessibility")
    private fun setupDraggableBottomPanel() {
        val draggableLayout = binding.root.findViewById<ConstraintLayout>(R.id.test)
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
                    layoutParams.height =
                        newHeight.coerceAtLeast(300) // 여기서 300은 최소 높이, 필요에 따라 조정 가능
                    view.layoutParams = layoutParams
                    lastY = event.rawY
                    true
                }

                else -> false
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

    // MapViewEventListener
    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.test)
        if (binInfoLayout.visibility == View.VISIBLE) {
            binInfoLayout.visibility = View.GONE
            Log.d("Map", "${binInfoLayout.visibility}")
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    // POIItemEventListener
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        val markerName = p1?.itemName ?: "Unknown"
        val binTitleTextView = binding.root.findViewById<TextView>(R.id.binTitle)
        binTitleTextView.text = markerName

        val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.test)
        binInfoLayout.visibility = View.VISIBLE

        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        // 뷰에 애니메이션 적용
        binInfoLayout.startAnimation(slideUpAnimation)
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
}
