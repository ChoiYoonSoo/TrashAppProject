package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
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
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentCameraBinding
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

class CameraFragment : Fragment() {

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var imageCapture: ImageCapture

    private lateinit var binding : FragmentCameraBinding

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

        // 뒤로가기 버튼 클릭 시
        binding.cameraBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cameraCloseBtn.setOnClickListener {
            binding.cameraContainer.visibility = View.GONE
        }

        startCamera()

        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)

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

        binding.cameraCaptureBtn.setOnClickListener {
            binding.cameraImage.setImageResource(R.drawable.camera_loading)
            val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.camera_slide_up)
            binding.cameraContainer.startAnimation(slideUpAnimation)
            binding.cameraContainer.visibility = View.VISIBLE
            takePhoto()
        }

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
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
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
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
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

}