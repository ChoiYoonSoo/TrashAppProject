package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentAdminBinBinding
import com.example.trashapp.view.adapter.AdminBinAdapter
import com.example.trashapp.viewmodel.AdminBinViewModel

class AdminBinFragment : Fragment() {

    private lateinit var binding : FragmentAdminBinBinding

    private val adminBinViewModel : AdminBinViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 애니메이션
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.base_scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        // 뒤로가기 버튼 애니메이션 적용
        binding.adminBinBackBtn.setOnTouchListener{ v, event ->
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

        // 아이템 클릭 했을 때 처리
        adminBinViewModel.isItemClicked.observe(viewLifecycleOwner){ isItemClicked ->
            if(isItemClicked){
                // 슬라이드 애니메이션으로 디테일 레이아웃 띄우기
                val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.camera_slide_up)
                binding.adminBinDetailLayout.startAnimation(slideUpAnimation)
                binding.adminBinDetailLayout.visibility = View.VISIBLE

                // 아이템 설정
                binding.trashCanId.text = adminBinViewModel.trachcanId
                binding.userId.text = adminBinViewModel.userId
                binding.location.text = adminBinViewModel.location
                binding.adminBinRV.visibility = View.GONE
                Log.d("관리자 쓰레기통 등록 이미지",adminBinViewModel.image)
                Glide.with(this)
                    .load(adminBinViewModel.image)
                    .into(binding.binImage)
            }
            else{
                binding.adminBinDetailLayout.visibility = View.GONE
                binding.adminBinRV.visibility = View.VISIBLE
            }
        }

        // 뒤로가기 버튼 클릭 시
        binding.adminBinBackBtn.setOnClickListener {
            adminBinViewModel.itemClicked(false)
            parentFragmentManager.popBackStack()
        }

        // 닫기 버튼 클릭
        binding.adminBinCloseBtn.setOnClickListener {
            adminBinViewModel.itemClicked(false)
        }

        // 사용자 신고목록 RecyclerView
        adminBinViewModel.adminBinList.observe(viewLifecycleOwner){ adminBinList ->
            if(adminBinList.isEmpty()){
                binding.adminBinRV.visibility = View.GONE
                binding.reportView.visibility = View.VISIBLE
            }
            else{
                binding.adminBinRV.visibility = View.VISIBLE
                binding.reportView.visibility = View.GONE
                binding.adminBinRV.adapter = AdminBinAdapter(adminBinList, adminBinViewModel)
                binding.adminBinRV.layoutManager = LinearLayoutManager(context)
            }
        }
    }
}