package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentCameraBinding
import com.example.trashapp.network.model.TrashcanLocation
import com.example.trashapp.view.SharedPreferencesManager
import com.example.trashapp.view.activities.MainActivity
import com.example.trashapp.viewmodel.CameraViewModel
import com.example.trashapp.viewmodel.CurrentGpsViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

class CameraFragment : Fragment() {

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var imageCapture: ImageCapture

    private lateinit var binding: FragmentCameraBinding

    private val currentGpsViewModel: CurrentGpsViewModel by activityViewModels()

    private val cameraViewModel: CameraViewModel by activityViewModels()

    private var isCameraReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraContainer.visibility = View.GONE

        binding.cameraImage.setImageResource(R.drawable.camera_loading)
        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.camera_slide_up)
        binding.cameraContainer.startAnimation(slideUpAnimation)

        buttonAnim()
        cameraViewModel.resetIsYoloSuccess()

        // 뒤로가기 버튼 클릭 시
        binding.cameraBackBtn.setOnClickListener {
            (activity as? MainActivity)?.stopLocationUpdates2()
            parentFragmentManager.popBackStack()
        }

        // 카메라 닫기 버튼 클릭 시
        binding.cameraCloseBtn.setOnClickListener {
            (activity as? MainActivity)?.stopLocationUpdates2()
            binding.cameraContainer.visibility = View.GONE
            binding.cameraCaptureBtn.visibility = View.VISIBLE
            binding.addressText.text = ""
            binding.addressEditText.setText("")
            binding.recyclingBin.isSelected = false
            binding.baseBin.isSelected = false
        }

        // 카메라 시작
        startCamera()

        // 토큰 및 현재 위치 받기
        val token = SharedPreferencesManager.getToken(requireContext())
        cameraViewModel.token = token
        currentGpsViewModel.currentLocation.observe(viewLifecycleOwner) {
            cameraViewModel.currentLatitude = it.latitude
            cameraViewModel.currentLongitude = it.longitude
        }

        // 카메라 캡처 버튼 클릭 시
        binding.cameraCaptureBtn.setOnClickListener {
            currentGpsViewModel.resetCurrentLocationList()
            cameraViewModel.category = null
            binding.cameraProgressBar.visibility = View.VISIBLE
            binding.cameraCaptureBtn.visibility = View.GONE
            (activity as? MainActivity)?.cameraStartLocationUpdates()
            takePhoto()
        }

        // 욜로 성공 여부
        cameraViewModel.isYoloSuccess.observe(viewLifecycleOwner){
            binding.cameraProgressBar.visibility = View.GONE
            binding.cameraCaptureBtn.visibility = View.VISIBLE
            if(it == true){
                binding.cameraContainer.visibility = View.VISIBLE
                binding.cameraCaptureBtn.visibility = View.GONE
            }
            else if(it == false){
                (activity as? MainActivity)?.stopLocationUpdates2()
                Toast.makeText(context, "쓰레기통을 정확히 찍어주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 재활용 쓰레기통 버튼 클릭 시
        binding.recyclingBin.setOnClickListener {
            binding.recyclingBin.isSelected = true
            binding.baseBin.isSelected = false
            cameraViewModel.category = "both"
            Log.d("재활용 쓰레기통 버튼 클릭", "카테고리 : ${cameraViewModel.category}")
        }

        // 일반 쓰레기통 버튼 클릭 시
        binding.baseBin.setOnClickListener {
            binding.recyclingBin.isSelected = false
            binding.baseBin.isSelected = true
            cameraViewModel.category = "general"
            Log.d("일반 쓰레기통 버튼 클릭", "카테고리 : ${cameraViewModel.category}")
        }


        // 쓰레기통 통신 성공 여부
        cameraViewModel.isSuccess.observe(viewLifecycleOwner) {
            currentGpsViewModel.resetCurrentLocationList()
            binding.cameraProgressBar.visibility = View.GONE
            binding.addressEditText.setText("")
            binding.recyclingBin.isSelected = false
            binding.baseBin.isSelected = false
            cameraViewModel.category = null
            if (it == true) {
                if(cameraViewModel.newTrashcanError == "10"){
                    Toast.makeText(context, "쓰레기통 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    cameraViewModel.resetIsSuccess()
                    parentFragmentManager.popBackStack()
                }
                else{
                    Toast.makeText(context, "이미 등록되어 있는 쓰레기통입니다.", Toast.LENGTH_SHORT).show()
                    cameraViewModel.resetIsSuccess()
                    parentFragmentManager.popBackStack()
                }

            } else if(it == false) {
                Toast.makeText(context, "잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                (activity as? MainActivity)?.stopLocationUpdates2()
                cameraViewModel.resetIsSuccess()
                binding.cameraContainer.visibility = View.GONE
                binding.cameraCaptureBtn.visibility = View.VISIBLE
                binding.addressText.text = ""
                binding.addressEditText.setText("")
                binding.recyclingBin.isSelected = false
                binding.baseBin.isSelected = false
            }
        }

        // 쓰레기통 등록 버튼 클릭 시
        binding.cameraReportBtn.setOnClickListener {
            if (cameraViewModel.category == null) {
                Toast.makeText(context, "쓰레기통 종류를 선택해주세요", Toast.LENGTH_SHORT).show()
            } else if (cameraViewModel.addressEditText == "") {
                Toast.makeText(context, "위치를 입력해주세요", Toast.LENGTH_SHORT).show()
            }else if(currentGpsViewModel.currentLocationList.size  == 0){
                Toast.makeText(context, "위치정보를 불러오고 있습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                binding.cameraProgressBar.visibility = View.VISIBLE
                Log.d(
                    "쓰레기통 등록 버튼 클릭",
                    "위도 : ${cameraViewModel.currentLatitude}, 경도 : ${cameraViewModel.currentLongitude})), 토큰 : ${cameraViewModel.token}"
                )
                (activity as? MainActivity)?.stopLocationUpdates2()

                if(currentGpsViewModel.currentLocationList.size == 1){
                    for (i in 0..3){
                        currentGpsViewModel.currentGpsList(currentGpsViewModel.currentLocationList[0].latitude, currentGpsViewModel.currentLocationList[0].longitude)
                    }
                }
                else if(currentGpsViewModel.currentLocationList.size == 2){
                    for (i in 0..2){
                        currentGpsViewModel.currentGpsList(currentGpsViewModel.currentLocationList[i].latitude, currentGpsViewModel.currentLocationList[i].longitude)
                    }
                }
                else if(currentGpsViewModel.currentLocationList.size == 3){
                    for (i in 0..1){
                        currentGpsViewModel.currentGpsList(currentGpsViewModel.currentLocationList[i].latitude, currentGpsViewModel.currentLocationList[i].longitude)
                    }
                }
                else if(currentGpsViewModel.currentLocationList.size == 4){
                    currentGpsViewModel.currentGpsList(currentGpsViewModel.currentLocationList[0].latitude, currentGpsViewModel.currentLocationList[0].longitude)
                }

                for(currentGps in currentGpsViewModel.currentLocationList){
                    Log.d("5개의 위도 경도 확인", "위도 : ${currentGps.latitude}, 경도 : ${currentGps.longitude}, 사이즈 : ${currentGpsViewModel.currentLocationList.size}")
                }

                var location = TrashcanLocation(
                    currentGpsViewModel.currentLocationList,
                    cameraViewModel.category!!,
                    cameraViewModel.addressEditText!!
                )
                val locationRequestBody = location.toJsonRequestBody()
                cameraViewModel.newTrashcan(
                    cameraViewModel.token!!,
                    locationRequestBody,
                    cameraViewModel.image!!
                )
            }
        }

        binding.addressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                cameraViewModel.addressEditText = s.toString()
            }
        })

    }

    // 카메라 API 사용
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder?.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                isCameraReady = true // 카메라 준비 완료
            } catch (exc: Exception) {
                Log.e("카메라 API", "실패", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // 이미지 캡처
    private fun takePhoto() {
        if (!isCameraReady) {
            Toast.makeText(context, "잠시만 기다려 주세요", Toast.LENGTH_SHORT).show()
            return
        }
        val photoFile = File(
            requireContext().getExternalFilesDir(null),
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss-SSS",
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("카메라 이미지 캡처", "성공 ${output.savedUri}")
                    // UI Thread에서 ImageView 업데이트
                    activity?.runOnUiThread {
                        updateImageView(Uri.fromFile(photoFile))
                    }

                    // 이미지 파일
                    val file = output.savedUri?.path?.let { File(it) }
                    // 파일이 null이 아닐 경우에만 업로드 수행
                    file?.let {
                        // 파일을 RequestBody로 변환
                        val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        Log.d("파일생성", "성공 ${requestFile}")

                        // MultipartBody.Part 생성
                        cameraViewModel.image =
                            MultipartBody.Part.createFormData("image", it.name, requestFile)
                        Log.d("MultipartBody.Part 생성", "성공 ${cameraViewModel.image}")
                        cameraViewModel.imageYolo(cameraViewModel.image!!)

                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("카메라 이미지 캡처", "실패 ${exc.message}", exc)
                }
            }
        )
    }

    // ImageView 업데이트
    private fun updateImageView(imageUri: Uri) {
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            // 비트맵 로딩을 위한 스트림
            inputStream = requireContext().contentResolver.openInputStream(imageUri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()  // 첫 번째 스트림 닫기

            // Exif 정보를 위한 새로운 스트림
            inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            inputStream.close()  // 두 번째 스트림 닫기

            if (bitmap == null) {
                Log.e("ImageView 업데이트", "비트맵 이미지 로딩 실패")
                return
            }

            val rotatedBitmap = rotateBitmapIfNeeded(bitmap, orientation)

            // UI 스레드에서 ImageView 업데이트
            activity?.runOnUiThread {
                binding.cameraImage.setImageBitmap(rotatedBitmap)
            }
        } catch (e: Exception) {
            Log.e("ImageView 업데이트", "실패", e)
        } finally {
            inputStream?.close()
        }
    }

    // 이미지 회전
    private fun rotateBitmapIfNeeded(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun TrashcanLocation.toJsonRequestBody(): RequestBody {
        val gson = Gson()
        val json = gson.toJson(this)
        return json.toRequestBody("application/json".toMediaTypeOrNull())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonAnim(){
        // 카메라 버튼 애니메이션 효과
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)

        // 뒤로가기 버튼 클릭 애니메이션
        binding.cameraBackBtn.setOnTouchListener{
                v, event ->
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

        // 카메라 캡처 버튼 터치 시
        binding.cameraCaptureBtn.setOnTouchListener { v, event ->
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

        // 쓰레기통 등록 버튼
        binding.cameraReportBtn.setOnTouchListener { v, event ->
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

        // 일반 쓰레기통 버튼
        binding.baseBin.setOnTouchListener{
                v, event ->
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

        // 일반/재활용 쓰레기통 버튼
        binding.recyclingBin.setOnTouchListener{
                v, event ->
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