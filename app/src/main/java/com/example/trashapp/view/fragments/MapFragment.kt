package com.example.trashapp.view.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trashapp.R
import com.example.trashapp.data.MapData
import com.example.trashapp.data.ReportItemData
import com.example.trashapp.databinding.FragmentMapBinding
import com.example.trashapp.factory.UserTokenViewModelFactory
import com.example.trashapp.network.model.GpsList
import com.example.trashapp.repository.UserTokenRepository
import com.example.trashapp.utils.hideKeyboard
import com.example.trashapp.view.activities.MainActivity
import com.example.trashapp.view.adapter.MapSearchAdapter
import com.example.trashapp.view.adapter.ReportItemAdapter
import com.example.trashapp.viewmodel.ApiListViewModel
import com.example.trashapp.viewmodel.CurrentGpsViewModel
import com.example.trashapp.viewmodel.UserInfoViewModel
import com.example.trashapp.viewmodel.UserTokenViewModel
import com.example.trashapp.viewmodel.WebViewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment(), MapView.MapViewEventListener, MapView.POIItemEventListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView              // 카카오 지도 뷰
    private var lastY: Float = 0.0f
    private var isEnable = false

    private lateinit var userTokenViewModel: UserTokenViewModel
    private val viewModel: ApiListViewModel by activityViewModels()
    private val userInfoViewModel: UserInfoViewModel by activityViewModels()
    private val webViewViewModel: WebViewViewModel by activityViewModels()
    private val currentGpsViewModel: CurrentGpsViewModel by activityViewModels()

    private val markerList: MutableList<MapPOIItem> = mutableListOf()
    private val CIRCLE_RADIUS = 15 // 원의 반지름을 미터 단위로 설정
    private val MIN_ZOOM_LEVEL_FOR_CIRCLE = -1 // 원을 표시할 최소 확대 레벨

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로딩 이미지 애니메이션
        val imageView = view.findViewById<ImageView>(R.id.loadingImage)
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.turn_around)
        imageView.startAnimation(animation)
        binding.loadingContainer.visibility = View.VISIBLE

        // 현재 위치로 이동 버튼
        binding.gpsBtn.setOnClickListener {
            val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.binInfoContainer)

            val reportList = binding.root.findViewById<View>(R.id.reportListContainer)
            reportList.visibility = View.GONE

            if (binInfoLayout.visibility == View.VISIBLE) {
                binInfoLayout.visibility = View.GONE
            }

            val searchRecyclerView = binding.root.findViewById<RecyclerView>(R.id.mapSearchRV)
            searchRecyclerView.visibility = View.GONE

            val roadViewContainer =
                binding.root.findViewById<ConstraintLayout>(R.id.roadViewContainer)
            roadViewContainer.visibility = View.GONE

            hideKeyboard()

            mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    currentGpsViewModel.location!!.latitude,
                    currentGpsViewModel.location!!.longitude
                ), true
            )
            isEnable = !isEnable
            if (isEnable) {
                binding.gpsBtn.setImageResource(R.drawable.select_gps)
            } else {
                binding.gpsBtn.setImageResource(R.drawable.gps)
            }
            currentGpsViewModel.setGpsEnabled(isEnable)
        }

        currentGpsViewModel.isGpsEnabled.observe(viewLifecycleOwner) { isEnabled ->
            if (isEnabled) {
                (activity as? MainActivity)?.startLocationUpdates()
            } else {
                (activity as? MainActivity)?.stopLocationUpdates()
            }
        }

        // Tmap 길찾기
        val tMapBtn = view.findViewById<Button>(R.id.tMapBtn)
        tMapBtn.setOnClickListener {
            (activity as? MainActivity)?.getLocation()
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_TMapFragment)
            binding.mapSearchEdit.setQuery("", false)
        }

        // UserTokenViewModel 의존성 주입
        val userRepository =
            UserTokenRepository(requireContext()) // UserTokenRepository 인스턴스를 생성하거나 의존성 주입을 통해 제공받습니다.
        val factory = UserTokenViewModelFactory(userRepository)
        userTokenViewModel = ViewModelProvider(this, factory).get(UserTokenViewModel::class.java)

        // 자동 로그인 토큰 확인
        if (userInfoViewModel.token != "") {
            val userToken = userInfoViewModel.token
            userInfoViewModel.getUserInfo(userToken)
            userTokenViewModel.saveToken(userToken)
        }

        mapView = binding.mapView   // 카카오 지도 뷰
        mapView.setMapViewEventListener(this)
        Log.d("맵 뷰 리스너", "ok")
        mapView.setPOIItemEventListener(this)

        // mapData 관찰 후 마커 표시
        viewModel.mapData.observe(viewLifecycleOwner) { mapData ->
            Log.d("맵데이터 관찰", "ok")
            if (mapData.isNotEmpty()) {
                Log.d("맵데이터 들어옴", mapData.toString())
                setMark(mapData)
            }
        }

        // 로드뷰 띄우기 사용 변수
        val reportImage = view.findViewById<ImageView>(R.id.binLoadImage)
        val roadView = view.findViewById<ImageView>(R.id.roadView)
        val roadViewContainer = view.findViewById<ConstraintLayout>(R.id.roadViewContainer)

        // 신고 버튼 클릭 시 이벤트
        val reportButton = view.findViewById<Button>(R.id.binReportBtn)
        val reportList = view.findViewById<View>(R.id.reportListContainer)
        if (userTokenViewModel.getToken() != null) {
            reportButton.isEnabled = true
            reportButton.setOnClickListener {
                binding.roadViewContainer.visibility = View.GONE
                if (reportList.visibility == View.GONE) {
                    reportList.visibility = View.VISIBLE
                }
            }
        } else {
            reportButton.isEnabled = false
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
        viewModel.placeList.observe(viewLifecycleOwner) { placeList ->
            searchRecyclerView.adapter = MapSearchAdapter(placeList, viewModel)
            searchRecyclerView.layoutManager = LinearLayoutManager(context)
            if (placeList.isEmpty()) {
                searchRecyclerView.visibility = View.GONE
            } else {
                searchRecyclerView.visibility = View.VISIBLE
            }
        }

        // 검색창에서 선택한 위치로 이동
        viewModel.selectPlace.observe(viewLifecycleOwner) { selectPlace ->
            binding.mapSearchEdit.setQuery("", false)
            isEnable = false
            currentGpsViewModel.setGpsEnabled(isEnable)
            val mapPoint = MapPoint.mapPointWithGeoCoord(
                selectPlace!!.y.toDouble(),
                selectPlace!!.x.toDouble()
            )
            mapView.setMapCenterPoint(mapPoint, true)
            searchRecyclerView.visibility = View.GONE
            binding.refreshBtn.visibility = View.VISIBLE
        }

        // SearchView QueryHint 설정
        val searchView = binding.mapSearchEdit
        searchView.queryHint =
            Html.fromHtml("<font color = #EDEDED>" + resources.getString(R.string.searchText) + "</font>")

        binding.mapSearchEdit.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.refreshBtn.visibility = View.GONE
                Log.d("검색어", newText.toString())
                if (newText.toString().isNotEmpty()) {
                    viewModel.getKaKaoKeyword(
                        "KakaoAK 9f69cb1d980918e3b79b5d8c7b49892c",
                        newText.toString()
                    )
                } else {
                    searchRecyclerView.visibility = View.GONE
                }
                return true
            }
        })

        // 설정 버튼 클릭 시 이벤트
        binding.mapSettingBtn.setOnClickListener {
            binding.mapSearchEdit.setQuery("", false)
            isEnable = false
            currentGpsViewModel.setGpsEnabled(isEnable)
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_settingFragment)
        }

        // 현 지도에서 다시 검색 버튼 이벤트
        binding.refreshBtn.setOnClickListener {
            getCurrentMapBounds()
            binding.refreshBtn.visibility = View.GONE
            binding.mapSearchRV.visibility = View.GONE
            hideKeyboard()
        }

        // 로드뷰 이미지 띄우기
        reportImage.setOnClickListener {
            roadViewContainer.visibility = View.VISIBLE
            reportList.visibility = View.GONE
            val imageUrl = viewModel.selectMapData?.imageUrl
            if (imageUrl!!.length > 10) {
                Glide.with(roadView.context)
                    .load(imageUrl)
                    .into(roadView)
            }
        }

        binding.roadViewMoveBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_webViewFragment)
        }
    }

    // 지도 마커 띄우기
    private fun setMark(dataList: List<MapData>) {
        Log.d("마커찍기", "ok")
        for (data in dataList) {
            Log.d("마커찍기데이터", data.toString())
            var marker = MapPOIItem()
            marker.apply {
                itemName = data.name
                mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
                customImageBitmap = getBitmapFromVectorDrawable(R.drawable.bin_marker)
                markerType = MapPOIItem.MarkerType.CustomImage
                userObject = data
                isShowCalloutBalloonOnTouch = false
                customSelectedImageBitmap =
                    getBitmapFromVectorDrawable(R.drawable.bin_marker_selected)
                selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                markerList.add(marker)
            }
            mapView.addPOIItem(marker)
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

    // 지도의 현재 보이는 영역의 좌표 범위를 가져오는 함수
    private fun getCurrentMapBounds() {
        val mapPointBounds = mapView.mapPointBounds
        Log.d("현재 지도의 좌표 범위", mapPointBounds.toString())
        val swLatLng = mapPointBounds.bottomLeft // 남서쪽(SW) 좌표
        val neLatLng = mapPointBounds.topRight // 북동쪽(NE) 좌표

        val swLat = swLatLng.mapPointGeoCoord.latitude
        val swLng = swLatLng.mapPointGeoCoord.longitude
        val neLat = neLatLng.mapPointGeoCoord.latitude
        val neLng = neLatLng.mapPointGeoCoord.longitude

        val gpsList = GpsList(swLat, swLng, neLat, neLng)
        Log.d("현재 범위의 GPS List", gpsList.toString())
        viewModel.getGpsList(gpsList)
    }

    // 마커 위치를 기준으로 원을 생성하고 지도에 추가하는 함수
    fun addCirclesAroundMarkers(mapView: MapView, markers: List<MapPOIItem>, radius: Int) {
        mapView.removeAllCircles()
        markers.forEachIndexed { index, marker ->
            // 마커의 위치를 가져옵니다.
            val markerPoint = marker.mapPoint

            // 원 객체를 생성합니다.
            val mapCircle = MapCircle(
                markerPoint,  // 원의 중심좌표
                radius,  // 원의 반지름(미터 단위)
                Color.argb(128, 135, 206, 250), // 선 색상을 하늘색으로, 반투명
                Color.argb(70, 135, 206, 250)  // 채우기 색깔을 반투명한 회색으로 설정
            ).apply {
                tag = index  // 각 원에 고유한 태그를 설정
            }
            // 지도에 원을 추가합니다.
            mapView.addCircle(mapCircle)
        }
    }

    // MapViewEventListener
    override fun onMapViewInitialized(p0: MapView?) {
        Log.d("맵뷰초기화", "ok")

        currentGpsViewModel.lastLocation.observe(viewLifecycleOwner) { location ->
            Log.d("currentGps!!!", "${location.latitude}, ${location.longitude}")

            // 기존 마커 제거
            mapView.findPOIItemByTag(1001)?.let {
                mapView.removePOIItem(it)
            }

            var marker = MapPOIItem()
            marker.apply {
                itemName = "현재 위치"
                mapPoint = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude)
                customImageBitmap = getBitmapFromVectorDrawable(R.drawable.current_gps)
                markerType = MapPOIItem.MarkerType.CustomImage
                isShowCalloutBalloonOnTouch = false
                tag = 1001
            }
            mapView.addPOIItem(marker)
        }

        currentGpsViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            binding.loadingContainer.visibility = View.GONE
            binding.gpsBtn.visibility = View.VISIBLE
            Log.d("lastGps!!!", "${location.latitude}, ${location.longitude}")
            mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    location.latitude,
                    location.longitude
                ), true
            )

            // 기존 마커 제거
            mapView.findPOIItemByTag(1001)?.let {
                mapView.removePOIItem(it)
            }

            var marker = MapPOIItem()
            marker.apply {
                itemName = "현재 위치"
                mapPoint = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude)
                customImageBitmap = getBitmapFromVectorDrawable(R.drawable.current_gps)
                markerType = MapPOIItem.MarkerType.CustomImage
                isShowCalloutBalloonOnTouch = false
                tag = 1001
            }
            mapView.addPOIItem(marker)
        }

        lifecycleScope.launch {
            // 중심점 설정 후 500밀리초(1초) 동안 기다립니다.
            delay(1000)
            getCurrentMapBounds()
        }
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, zoomLevel: Int) {
        if (zoomLevel <= MIN_ZOOM_LEVEL_FOR_CIRCLE) {
            addCirclesAroundMarkers(mapView, markerList, CIRCLE_RADIUS)
        } else {
            mapView.removeAllCircles()
        }
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

        val roadViewContainer = binding.root.findViewById<ConstraintLayout>(R.id.roadViewContainer)
        roadViewContainer.visibility = View.GONE

        hideKeyboard()
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        binding.refreshBtn.visibility = View.VISIBLE
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    // POIItemEventListener
    // 마커 클릭 시 이벤트
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        isEnable = false
        currentGpsViewModel.setGpsEnabled(isEnable)

        if (p1?.tag == 1001) {
            return
        }
        // 마커 이름 표시
        val markerName = p1?.itemName ?: "Unknown"
        val binTitleTextView = binding.root.findViewById<TextView>(R.id.binTitle)
        binTitleTextView.text = markerName

        // 마커 주소 표시
        val markerAddr = p1?.userObject as MapData
        val binAddrTextView = binding.root.findViewById<TextView>(R.id.binAddr)
        binAddrTextView.text = markerAddr.addr

        // 로드뷰 이미지 표시
        val markerImage = p1?.userObject as MapData
        val binImageView = binding.root.findViewById<ImageView>(R.id.binLoadImage)
        val imageUrl = markerImage.imageUrl

        if (imageUrl!!.length > 10) {
            Glide.with(binImageView.context)
                .load(imageUrl)
                .into(binImageView)
        }
        viewModel.selectMapData = markerImage

        val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.binInfoContainer)
        binInfoLayout.visibility = View.VISIBLE

        val reportList = binding.root.findViewById<View>(R.id.reportListContainer)
        reportList.visibility = View.GONE

        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        // 뷰에 애니메이션 적용
        binInfoLayout.startAnimation(slideUpAnimation)

        webViewViewModel.latitude = markerAddr.latitude
        webViewViewModel.longitude = markerAddr.longitude

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
