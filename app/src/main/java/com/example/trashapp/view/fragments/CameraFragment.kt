package com.example.trashapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentCameraBinding
import java.io.File
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        startCamera()

        binding.cameraCaptureBtn.setOnClickListener {
            takePhoto()
        }
    }

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
            Log.e("CameraFragment", "Use case binding failed", exc)
        }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        if (!isCameraReady) {
            Toast.makeText(context, "Camera is not ready yet. Please wait.", Toast.LENGTH_SHORT).show()
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
                    Log.d("카메라", "Photo capture succeeded: ${output.savedUri}")
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("카메라", "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

}