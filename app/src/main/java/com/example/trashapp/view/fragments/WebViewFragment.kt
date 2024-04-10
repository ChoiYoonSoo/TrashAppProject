package com.example.trashapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentWebViewBinding
import com.example.trashapp.viewmodel.WebViewViewModel

class WebViewFragment : Fragment() {

    private lateinit var binding: FragmentWebViewBinding

    private val webViewViewModel: WebViewViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            Log.d("웹뷰에 사용될 위도 경도", "latitude: ${webViewViewModel.latitude}, longitude: ${webViewViewModel.longitude}")
//            loadUrl("https://map.kakao.com/link/roadview/${webViewViewModel.latitude},${webViewViewModel.longitude}")
            loadUrl("https://map.kakao.com/link/roadview/37.47960336657709,126.8820342335089")
        }

        binding.webViewBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}