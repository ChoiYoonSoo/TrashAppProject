package com.example.trashapp.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trashapp.R
import com.example.trashapp.data.ReportItemData
import com.example.trashapp.databinding.FragmentMapBinding
import com.example.trashapp.factory.UserTokenViewModelFactory
import com.example.trashapp.repository.UserTokenRepository
import com.example.trashapp.utils.RequestPermissionsUtil
import com.example.trashapp.utils.hideKeyboard
import com.example.trashapp.view.activities.MainActivity
import com.example.trashapp.view.adapter.MapSearchAdapter
import com.example.trashapp.view.adapter.ReportItemAdapter
import com.example.trashapp.viewmodel.ApiListViewModel
import com.example.trashapp.viewmodel.CurrentGpsViewModel
import com.example.trashapp.viewmodel.UserInfoViewModel
import com.example.trashapp.viewmodel.UserTokenViewModel
import com.example.trashapp.viewmodel.WebViewViewModel
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback
import com.example.trashapp.BuildConfig
import com.example.trashapp.data.MapData
import com.example.trashapp.network.model.GpsList
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.LatLngBounds
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapView
import com.kakao.vectormap.Poi
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPointBounds

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    // KaKao
    private lateinit var mapView: MapView
    private var kaKaoMap: KakaoMap? = null

    // Label
    private var firstLabelLayer: LabelLayer? = null
    private var labelLayer: LabelLayer? = null
    private var firstLabel: Label? = null
    private var label: Label? = null

    // MapMarker
    private var selectedLabelList : MutableList<Label?> = mutableListOf()
    private var removeList: MutableList<Label?> = mutableListOf()
    private val markerList: MutableList<MapPOIItem> = mutableListOf()
    private var lastLatitude: Double = 0.0
    private var lastLongitude: Double = 0.0
    private var mapDataList: MutableList<MapData> = mutableListOf()

    private var isEnable = false
    private var lastY: Float = 0.0f
    private val CIRCLE_RADIUS = 15 // 원의 반지름을 미터 단위로 설정
    private val MIN_ZOOM_LEVEL_FOR_CIRCLE = -1 // 원을 표시할 최소 확대 레벨

    // ViewModel
    private lateinit var permissionsUtil: RequestPermissionsUtil
    private lateinit var userTokenViewModel: UserTokenViewModel
    private val viewModel: ApiListViewModel by activityViewModels()
    private val userInfoViewModel: UserInfoViewModel by activityViewModels()
    private val webViewViewModel: WebViewViewModel by activityViewModels()
    private val currentGpsViewModel: CurrentGpsViewModel by activityViewModels()
    private lateinit var requestCameraPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCameraPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    moveToCameraFragment()
                } else {
                    // 권한 거부 시 토스트 메시지 출력
                    Toast.makeText(context, "설정앱에서 카메라 권한을 허용하세요.", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 뒤로가기 버튼 동작을 제어하는 콜백 등록
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // 뒤로가기 버튼 동작을 막음
                }
            })

        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 카카오맵 SDK 초기화
        KakaoMapSdk.init(requireContext(), BuildConfig.KAKAO_MAP_KEY)

        mapView = binding.mapView
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 지도 준비가 완료되었을 때 호출됨
                // 예: 마커 추가, 초기 위치 설정 등
                kaKaoMap = kakaoMap

                if (lastLatitude != 0.0 && lastLongitude != 0.0) {
                    val point = LatLng.from(lastLatitude, lastLongitude)
                    val cameraUpdate = CameraUpdateFactory.newCenterPosition(point)
                    // 카메라 이동 후 라벨 추가
                    kakaoMap.moveCamera(cameraUpdate)
                    removeMark()
                    getCurrentMapBounds()
                    Log.d("시작위치", point.toString())
                }

                kakaoMap.setOnMapClickListener(object : KakaoMap.OnMapClickListener {
                    override fun onMapClicked(p0: KakaoMap, p1: LatLng, p2: PointF, p3: Poi) {
                        for(i in selectedLabelList){
                            i?.changeStyles(setLabelStyle(true))
                        }
                        selectedLabelList.clear()
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
                })

                kakaoMap.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
                    override fun onLabelClicked(p0: KakaoMap?, p1: LabelLayer?, p2: Label?): Boolean {
                            if(p2?.tag != null && p2.tag != 1001){
                                for(i in selectedLabelList){
                                    i?.changeStyles(setLabelStyle(true))
                                }
                                selectedLabelList.clear()
                                selectedLabelList.add(p2)
                                p2.changeStyles(setLabelStyle(false))
                                viewModel.id = mapDataList[p2.tag as Int].id
                                // 신고 횟수 API 호출
                                viewModel.findReportCount(viewModel.id)
                                isEnable = false
                                currentGpsViewModel.setGpsEnabled(isEnable)
                                binding.gpsBtn.setImageResource(R.drawable.gps)

                                if (p2.tag == 1001) {
                                    return false
                                }

                                val binTitleTextView = binding.root.findViewById<TextView>(R.id.binTitle)
                                binTitleTextView.text = mapDataList[p2.tag as Int].name

                                val binAddrTextView = binding.root.findViewById<TextView>(R.id.binAddr)
                                binAddrTextView.text = mapDataList[p2.tag as Int].addr

                                // 로드뷰 이미지 표시
                                val binImageView = binding.root.findViewById<ImageView>(R.id.binLoadImage)
                                val imageUrl = mapDataList[p2.tag as Int].imageUrl

                                if (imageUrl!!.length > 10) {
                                    Glide.with(binImageView.context)
                                        .load(imageUrl)
                                        .into(binImageView)
                                }
                                viewModel.selectMapData = mapDataList[p2.tag as Int]

                                // 닉네임 표시
                                val binNicknameTextView = binding.root.findViewById<TextView>(R.id.binNickname)
                                val fullText = "${mapDataList[p2.tag as Int].nickname}}님께서 발견한 쓰레기통입니다."
                                val spannableString = SpannableString(fullText)

                                val boldSpan = StyleSpan(Typeface.BOLD) // 굵은 글씨 스타일
                                val greyColor = ContextCompat.getColor(requireContext(), R.color.grey)
                                val colorSpan = ForegroundColorSpan(greyColor) // 색상 변경
                                val sizeSpan = RelativeSizeSpan(1.25f) // 글자 크기 1.5배로 설정

                                // "nickname.nickname" 단어의 시작과 끝 인덱스를 찾습니다.
                                val start = fullText.indexOf(mapDataList[p2.tag as Int].nickname!!)
                                val end = start + mapDataList[p2.tag as Int].nickname!!.length

                                // "nickname.nickname" 부분에 스타일 적용
                                spannableString.setSpan(boldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                spannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                                // TextView에 SpannableString 설정
                                binNicknameTextView.text = spannableString

                                // 쓰레기통 종류 표시하기
                                val bothBinButton = binding.root.findViewById<Button>(R.id.bothBinCategory)
                                val baseBinButton = binding.root.findViewById<Button>(R.id.baseBinCategory)
                                if(mapDataList[p2.tag as Int].category == "both"){
                                    bothBinButton.visibility = View.VISIBLE
                                }
                                else{
                                    baseBinButton.visibility = View.VISIBLE
                                }

                                val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.binInfoContainer)
                                binInfoLayout.visibility = View.VISIBLE

                                val reportList = binding.root.findViewById<View>(R.id.reportListContainer)
                                reportList.visibility = View.GONE

                                val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
                                // 뷰에 애니메이션 적용
                                binInfoLayout.startAnimation(slideUpAnimation)

                                webViewViewModel.latitude = mapDataList[p2.tag as Int].latitude
                                webViewViewModel.longitude = mapDataList[p2.tag as Int].longitude
                                Log.d("웹 뷰 위도 경도", "latitude: ${webViewViewModel.latitude}, longitude: ${webViewViewModel.longitude}")
                            }
                        return true
                    }
                })

                kakaoMap.setOnCameraMoveEndListener(object : KakaoMap.OnCameraMoveEndListener {
                    override fun onCameraMoveEnd(p0: KakaoMap, p1: CameraPosition, p2: GestureType) {
                        lastLatitude = p1.position.latitude
                        lastLongitude = p1.position.longitude
                        binding.refreshBtn.visibility = View.VISIBLE

                        if(!currentGpsViewModel.isMapPoint){
                            getCurrentMapBounds()
                            currentGpsViewModel.isMapPoint = true
                        }
                    }
                })

                currentGpsViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
                    binding.loadingContainer.visibility = View.GONE
                    binding.createBinBtn.visibility = View.VISIBLE
                    binding.gpsBtn.visibility = View.VISIBLE

                    if(firstLabel != null){
                        firstLabelLayer?.remove(firstLabel)
                    }

                    firstLabelLayer = kakaoMap.labelManager?.layer

                    val latLng = LatLng.from(location.latitude, location.longitude)

                    // 카메라를 (locationY, locationX) 위치로 이동시키는 업데이트 생성
                    val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)

                    // 지도에 표시할 라벨의 스타일 설정
                    val style = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.current_gps2)))
                    val options = LabelOptions.from(latLng).setStyles(style).setTag(1001)

                    firstLabel = firstLabelLayer?.addLabel(options)

                    // 카메라 이동 후 라벨 추가
                    kakaoMap.moveCamera(cameraUpdate)

                }

                currentGpsViewModel.lastLocation.observe(viewLifecycleOwner) { location ->
                    Log.d("currentGps!!!", "${location.latitude}, ${location.longitude}")
                    // 기존 마커 제거
                    if(firstLabel != null){
                        firstLabelLayer?.remove(firstLabel)
                    }
                    val latLng = LatLng.from(location.latitude, location.longitude)

                    // 지도에 표시할 라벨의 스타일 설정
                    val style = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.current_gps2)))
                    val options = LabelOptions.from(latLng).setStyles(style)

                    firstLabel = firstLabelLayer?.addLabel(options)
                }
            }

        })

        buttonAnim()

        // 신고 성공 변화 감지
        viewModel.isReportSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                when (viewModel.reportError) {
                    "5" -> {
                        Toast.makeText(context, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                    "3" -> {
                        Toast.makeText(context, "신고는 1분에 1번만 가능합니다.", Toast.LENGTH_SHORT).show()
                    }

                    "4" -> {
                        Toast.makeText(context, "목록당 1번의 신고만 가능합니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetReportSuccess()
            } else if (isSuccess == false) {
                Toast.makeText(context, "잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                viewModel.resetReportSuccess()
            }
        }

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

            val latLng = LatLng.from(currentGpsViewModel.location!!.latitude, currentGpsViewModel.location!!.longitude)

            // 카메라를 (locationY, locationX) 위치로 이동시키는 업데이트 생성
            val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
            kaKaoMap?.moveCamera(cameraUpdate)

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

        // mapData 관찰 후 마커 표시
        viewModel.mapData.observe(viewLifecycleOwner) { mapData ->
            Log.d("맵데이터 관찰", "ok")
            if (mapData.isNotEmpty()) {
                Log.d("맵데이터 들어옴", mapData.toString())
                setMark(mapData)
            } else {
                Log.d("맵데이터 없음", "실패")
            }
        }

        // 로드뷰 띄우기 사용 변수
        val reportImage = view.findViewById<ImageView>(R.id.binLoadImage)
        val roadView = view.findViewById<ImageView>(R.id.roadView)
        val roadViewContainer = view.findViewById<ConstraintLayout>(R.id.roadViewContainer)

        // 신고 버튼 클릭 시 이벤트
        val reportButton = view.findViewById<Button>(R.id.binReportBtn)
        val reportList = view.findViewById<View>(R.id.reportListContainer)
        val reportCount = view.findViewById<TextView>(R.id.reportCount)
        reportButton.setOnClickListener {
            if (userTokenViewModel.getToken() != null) {
                reportCount.text = viewModel.reportCount.toString()
                binding.roadViewContainer.visibility = View.GONE
                if (reportList.visibility == View.GONE) {
                    reportList.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(context, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 신고 RecyclerView 초기화
        val reportRecyclerView = view.findViewById<RecyclerView>(R.id.reportRV)
        val reportTextList = ArrayList<ReportItemData>()
        reportTextList.add(ReportItemData("지도에 나온 위치와 다른 곳에 있어요."))
        reportTextList.add(ReportItemData("지도에 나온 위치에 없어요."))
        reportRecyclerView.adapter = ReportItemAdapter(reportTextList, viewModel)
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

        // 쓰레기통 신고 횟수
        viewModel.trashcanReportCount.observe(viewLifecycleOwner) { count ->
            reportCount.text = count.toString()
        }

        // 카메라 권한 설정
        permissionsUtil = RequestPermissionsUtil(requireActivity())

        // 쓰레기통 등록 버튼 클릭 시
        binding.createBinBtn.setOnClickListener {
            if (userTokenViewModel.getToken() != null) {
                // 권한 상태 검사 후 필요시 요청
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // 권한이 이미 있으면 카메라 실행
                        Navigation.findNavController(view)
                            .navigate(R.id.action_mapFragment_to_cameraFragment)
                    }

                    else -> {
                        // 권한 요청
                        requestCameraPermission.launch(Manifest.permission.CAMERA)
                    }
                }
            } else {
                Toast.makeText(context, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 검색창에서 선택한 위치로 이동
        viewModel.selectPlace.observe(viewLifecycleOwner) { selectPlace ->
            binding.mapSearchEdit.setQuery("", false)
            isEnable = false
            currentGpsViewModel.setGpsEnabled(isEnable)

            // 카메라를 (locationY, locationX) 위치로 이동시키는 업데이트 생성
            val latLng = LatLng.from(selectPlace!!.y.toDouble(), selectPlace!!.x.toDouble())
            val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
            kaKaoMap?.moveCamera(cameraUpdate)
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
                val binInfoLayout = binding.root.findViewById<ConstraintLayout>(R.id.binInfoContainer)
                if (binInfoLayout.visibility == View.VISIBLE) {
                    binInfoLayout.visibility = View.GONE
                }
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
            removeMark()
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

        // 로드뷰 이동 버튼
        binding.roadViewMoveBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_webViewFragment)
        }
    }

    private fun setLabelStyle(check : Boolean): LabelStyles? {
        if (check){
            return kaKaoMap?.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.bin)))
        }
        else{
            return kaKaoMap?.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.binselect)))
        }
    }

    private fun moveToCameraFragment() {
        view?.let {
            Navigation.findNavController(it).navigate(R.id.action_mapFragment_to_cameraFragment)
        }
    }

    // 지도 마커 삭제
    private fun removeMark() {
        if (removeList.isNotEmpty()) {
            for (data in removeList) {
                labelLayer?.remove(data)
            }
            removeList.clear()
        }

        if (mapDataList.isNotEmpty()) {
            mapDataList.clear()
        }
    }

    // 지도 마커 띄우기
    private fun setMark(dataList: List<MapData>) {
        Log.d("마커찍기", "ok")
        for (d in dataList){
            mapDataList.add(d)

        }
        labelLayer = kaKaoMap?.labelManager?.layer
        for ((index, data) in mapDataList.withIndex()) {
            Log.d("마커찍기데이터", data.toString())

            val latLng = LatLng.from(data.latitude, data.longitude)
            val options = LabelOptions.from(latLng).setStyles(setLabelStyle(true)).setTag(index)

            label = labelLayer?.addLabel(options)

            removeList.add(label)
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

    /** 지도의 현재 보이는 영역의 좌표 범위를 가져오는 함수 **/
    private fun getCurrentMapBounds() {
        val pos1 : LatLng? = kaKaoMap?.fromScreenPoint(mapView.right, mapView.top)
        val pos2 : LatLng? = kaKaoMap?.fromScreenPoint(mapView.left, mapView.bottom)
        Log.d("범위", "pos1 : $pos1, pos2 : $pos2")

        val gpsList = GpsList(pos2!!.latitude, pos2.longitude, pos1!!.latitude, pos1.longitude)
        Log.d("현재 범위의 GPS List", gpsList.toString())
        viewModel.getGpsList(gpsList)
    }

    /** 마커 위치를 기준으로 원을 생성하고 지도에 추가하는 함수 **/
//    private fun addCirclesAroundMarkers(mapView: MapView, markers: List<MapPOIItem>?, radius: Int) {
//        mapView.removeAllCircles()
//        markers?.forEachIndexed { index, marker ->
//            // 마커의 위치를 가져옵니다.
//            val markerPoint = marker.mapPoint
//
//            // 원 객체를 생성합니다.
//            val mapCircle = MapCircle(
//                markerPoint,  // 원의 중심좌표
//                radius,  // 원의 반지름(미터 단위)
//                Color.argb(128, 135, 206, 250), // 선 색상을 하늘색으로, 반투명
//                Color.argb(70, 145, 216, 255)  // 약간 더 밝고, 블루 색상을 최대로
//            ).apply {
//                tag = index  // 각 원에 고유한 태그를 설정
//            }
//            // 지도에 원을 추가합니다.
//            mapView.addCircle(mapCircle)
//        }
//    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonAnim() {
        // 애니메이션
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)

        binding.mapSettingBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(scaleDown)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.startAnimation(scaleUp)
                    v.performClick()
                    true
                }

                else -> false
            }
        }

        binding.roadViewMoveBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(scaleDown)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.startAnimation(scaleUp)
                    v.performClick()
                    true
                }

                else -> false
            }
        }

        binding.createBinBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(scaleDown)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.startAnimation(scaleUp)
                    v.performClick()
                    true
                }

                else -> false
            }
        }

        binding.gpsBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(scaleDown)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.startAnimation(scaleUp)
                    v.performClick()
                    true
                }

                else -> false
            }
        }

        val binImageView = binding.root.findViewById<ImageView>(R.id.binLoadImage)
        binImageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(scaleDown)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.startAnimation(scaleUp)
                    v.performClick()
                    true
                }

                else -> false
            }
        }

        val tmapBtn = binding.root.findViewById<Button>(R.id.tMapBtn)
        tmapBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(scaleDown)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.startAnimation(scaleUp)
                    v.performClick()
                    true
                }

                else -> false
            }
        }

        val binReportBtn = binding.root.findViewById<Button>(R.id.binReportBtn)
        binReportBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(scaleDown)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.startAnimation(scaleUp)
                    v.performClick()
                    true
                }

                else -> false
            }
        }
    }
}

//    override fun onMapViewZoomLevelChanged(p0: MapView?, zoomLevel: Int) {
//        if (zoomLevel <= MIN_ZOOM_LEVEL_FOR_CIRCLE) {
//            addCirclesAroundMarkers(mapView, markerList, CIRCLE_RADIUS)
//        } else {
//            mapView.removeAllCircles()
//        }
//    }
