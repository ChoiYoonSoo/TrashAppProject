package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentAdminReportBinding
import com.example.trashapp.network.model.ModifyTrashcan
import com.example.trashapp.view.adapter.AdminReportAdapter
import com.example.trashapp.viewmodel.AdminReportViewModel

class AdminReportFragment : Fragment() {

    private lateinit var binding : FragmentAdminReportBinding

    private val viewModel : AdminReportViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminReportBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 애니메이션
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.base_scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        // 뒤로가기 버튼 클릭 시
        binding.adminReportBackBtn.setOnClickListener {
            viewModel.itemClicked(false)
            parentFragmentManager.popBackStack()
        }

        // 뒤로가기 버튼 애니메이션 적용
        binding.adminReportBackBtn.setOnTouchListener{ v, event ->
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

        // 닫기 버튼 클릭
        binding.adminReportCloseBtn.setOnClickListener {
            viewModel.itemClicked(false)
        }

        viewModel.isItemClicked.observe(viewLifecycleOwner){ isItemClicked ->
            if(isItemClicked){
                val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.camera_slide_up)
                binding.adminReportDetailLayout.startAnimation(slideUpAnimation)
                binding.adminReportDetailLayout.visibility = View.VISIBLE
                binding.reportTrashCanId.text = viewModel.trashcanId.toString()
                binding.reportUserId.text = viewModel.userId.toString()
                binding.adminReportRV.visibility = View.GONE
            }
            else{
                viewModel.isModify()
                viewModel.latitude = ""
                viewModel.longitude = ""
                binding.reportLatitude.setText("")
                binding.reportLongitude.setText("")
                binding.adminReportDetailLayout.visibility = View.GONE
                binding.adminReportRV.visibility = View.VISIBLE

            }
        }

        // 쓰레기통 수정 완료 처리
        viewModel.isModify.observe(viewLifecycleOwner){ isModify ->
            if(isModify == true){
                viewModel.itemClicked(false)
                Toast.makeText(context,"신고 처리 완료되었습니다.",Toast.LENGTH_SHORT).show()
            }
            else if(isModify == false){
                Log.d("쓰레기통 수정 API 통신","error")
            }
        }

        // 수정 완료 버튼 클릭 시
        binding.adminReportBtn.setOnClickListener {
            if(viewModel.isSuccess.value == true){
                // 쓰레기통 정보와 위도 경도 보내는 API 통신 필요
                val modifyTrashcan = ModifyTrashcan(viewModel.reportId.toString(),viewModel.trashcanId.toString(),viewModel.latitude.toDouble(),viewModel.longitude.toDouble())
                viewModel.modifyTrashcan(modifyTrashcan)
            }
            else{
                Toast.makeText(context,"위도와 경도를 입력해주세요",Toast.LENGTH_SHORT).show()
            }
        }

        // 위도 입력 텍스트 필드
        binding.reportLatitude.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(viewModel.latitude.isNotEmpty() && viewModel.longitude.isNotEmpty()){
                    viewModel.isSuccess(true)
                }
                else{
                    viewModel.isSuccess(false)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                viewModel.latitude = s.toString()
            }
        })

        // 경도 입력 텍스트 필드
        binding.reportLongitude.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(viewModel.latitude.isNotEmpty() && viewModel.longitude.isNotEmpty()){
                    viewModel.isSuccess(true)
                }
                else{
                    viewModel.isSuccess(false)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                viewModel.longitude = s.toString()
            }
        })

        // 사용자 신고목록 RecyclerView
        viewModel.adminReportList.observe(viewLifecycleOwner){ adminReportList ->
            binding.adminReportRV.adapter = AdminReportAdapter(adminReportList, viewModel)
            binding.adminReportRV.layoutManager = LinearLayoutManager(context)
        }

    }
}