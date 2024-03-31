package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.R
import com.example.trashapp.data.MapData
import com.example.trashapp.data.ReportItemData
import com.example.trashapp.databinding.FragmentMapBinding
import com.example.trashapp.utils.hideKeyboard
import com.example.trashapp.view.adapter.MapSearchAdapter
import com.example.trashapp.view.adapter.ReportItemAdapter
import com.example.trashapp.viewmodel.ApiListViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment(), MapView.MapViewEventListener, MapView.POIItemEventListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView              // 카카오 지도 뷰
    private var lastY: Float = 0.0f
    private val viewModel: ApiListViewModel by activityViewModels()

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
        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)

        // mapData 관찰 후 마커 표시
        viewModel.mapData.observe(viewLifecycleOwner) { mapData ->
            if (mapData != null) {
                setMark(mapData)
            }
        }

        // 신고 버튼 클릭 시 이벤트
        val reportButton = view.findViewById<Button>(R.id.binReportBtn)
        val reportList = view.findViewById<View>(R.id.reportListContainer)
        reportButton.setOnClickListener {
            if (reportList.visibility == View.GONE) {
                reportList.visibility = View.VISIBLE
            }
        }

        // 신고 RecyclerView 초기화
        val reportRecyclerView = view.findViewById<RecyclerView>(R.id.reportRV)
        val reportTextList = ArrayList<ReportItemData>()
        reportTextList.add(ReportItemData("지도에 나온 위치에 정확히 있었어요."))
        reportTextList.add(ReportItemData("분리수거 할 수 있게 되어 있어요."))
        reportTextList.add(ReportItemData("쓰레기가 꽉 차 있어 버릴 수 없어요."))
        reportTextList.add(ReportItemData("지도에 나온 위치와 다른 곳에 있어요."))
        reportTextList.add(ReportItemData("지도에 나온 위치에 없어요."))
        reportRecyclerView.adapter = ReportItemAdapter(reportTextList)
        reportRecyclerView.layoutManager = LinearLayoutManager(context)

        // 검색 RecyclerView 초기화
        val searchRecyclerView = view.findViewById<RecyclerView>(R.id.mapSearchRV)
        searchRecyclerView.adapter = MapSearchAdapter(viewModel.mapData.value ?: emptyList(), viewModel)
        searchRecyclerView.layoutManager = LinearLayoutManager(context)

        // 설정 버튼 클릭 시 이벤트
        binding.mapSettingBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_settingFragment)
        }

        // 검색창 클릭 시 이벤트
        binding.mapSearchEdit.setOnClickListener {
            val searchRecyclerView = view.findViewById<RecyclerView>(R.id.mapSearchRV)
            searchRecyclerView.visibility = View.VISIBLE
        }
    }

    // 지도 마커 띄우기
    private fun setMark(dataList: List<MapData>) {
        for (data in dataList) {
            var marker = MapPOIItem()
            marker.apply {
                itemName = data.name
                mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
                customImageBitmap = getBitmapFromVectorDrawable(R.drawable.bin_marker)
                markerType = MapPOIItem.MarkerType.CustomImage
                userObject = data.addr
                isShowCalloutBalloonOnTouch = false
                customSelectedImageBitmap = getBitmapFromVectorDrawable(R.drawable.bin_marker_selected)
                selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            }
//            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.49855955, 127.0444754), true)
            mapView.addPOIItem(marker)
        }
        viewModel.selectMapData.observe(viewLifecycleOwner) { mapData ->
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mapData.latitude, mapData.longitude), true)
        }
    }

    // 스크롤 이동
    @SuppressLint("ClickableViewAccessibility")
    private fun setupDraggableBottomPanel() {
        val draggableLayout = binding.root.findViewById<ConstraintLayout>(R.id.binInfoContainer)
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

    // 지도 빈공간 클릭 시 이벤트
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.binInfoContainer)

        val reportList = binding.root.findViewById<View>(R.id.reportListContainer)
        reportList.visibility = View.GONE

        if (binInfoLayout.visibility == View.VISIBLE) {
            binInfoLayout.visibility = View.GONE
        }

        val searchRecyclerView = binding.root.findViewById<RecyclerView>(R.id.mapSearchRV)
        searchRecyclerView.visibility = View.GONE

        hideKeyboard()
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
    // 마커 클릭 시 이벤트
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        // 마커 이름 표시
        val markerName = p1?.itemName ?: "Unknown"
        val binTitleTextView = binding.root.findViewById<TextView>(R.id.binTitle)
        binTitleTextView.text = markerName

        // 마커 주소 표시
        val markerAddr = p1?.userObject as? String
        val binAddrTextView = binding.root.findViewById<TextView>(R.id.binAddr)
        binAddrTextView.text = markerAddr

        val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.binInfoContainer)
        binInfoLayout.visibility = View.VISIBLE

        val reportList = binding.root.findViewById<View>(R.id.reportListContainer)
        reportList.visibility = View.GONE

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
