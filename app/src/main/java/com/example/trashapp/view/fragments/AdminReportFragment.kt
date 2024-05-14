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
import com.example.trashapp.network.model.CancelReport
import com.example.trashapp.network.model.ModifyTrashcan
import com.example.trashapp.view.adapter.AdminReportAdapter
import com.example.trashapp.viewmodel.AdminReportViewModel

class AdminReportFragment : Fragment() {

    private lateinit var binding : FragmentAdminReportBinding

    private val adminReportViewModel : AdminReportViewModel by activityViewModels()

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
            adminReportViewModel.itemClicked(false)
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
            adminReportViewModel.itemClicked(false)
        }

        // 취소 클릭 처리
        adminReportViewModel.isCancelClicked.observe(viewLifecycleOwner){ isCancelClicked ->
            if(isCancelClicked){
                // 슬라이드 애니메이션으로 디테일 레이아웃 보이기
                val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.camera_slide_up)
                binding.adminCancelDetailLayout.startAnimation(slideUpAnimation)
                binding.adminCancelDetailLayout.visibility = View.VISIBLE

                binding.adminReportRV.visibility = View.GONE
            }else{
                adminReportViewModel.cancelText = ""
                binding.cancelText.setText("")
                binding.adminCancelDetailLayout.visibility = View.GONE
                binding.adminReportRV.visibility = View.VISIBLE
            }
        }

        // 아이템 클릭 처리
        adminReportViewModel.isItemClicked.observe(viewLifecycleOwner){ isItemClicked ->
            if(isItemClicked){
                // 슬라이드 애니메이션으로 디테일 레이아웃 보이기
                val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.camera_slide_up)
                binding.adminReportDetailLayout.startAnimation(slideUpAnimation)
                binding.adminReportDetailLayout.visibility = View.VISIBLE

                // 아이템 설정
                binding.reportTrashCanId.text = adminReportViewModel.trashcanId.toString()
                binding.reportUserId.text = adminReportViewModel.userId.toString()
                binding.adminReportRV.visibility = View.GONE
            }
            else{
                // 초기화
                adminReportViewModel.isModify()
                adminReportViewModel.latitude = ""
                adminReportViewModel.longitude = ""
                binding.reportLatitude.setText("")
                binding.reportLongitude.setText("")
                binding.adminReportDetailLayout.visibility = View.GONE
                binding.adminReportRV.visibility = View.VISIBLE

            }
        }

        // 쓰레기통 수정 완료 처리
        adminReportViewModel.isModify.observe(viewLifecycleOwner){ isModify ->
            if(isModify == true){
                adminReportViewModel.itemClicked(false)
                Toast.makeText(context,"신고 처리 완료되었습니다.",Toast.LENGTH_SHORT).show()
            }
            else if(isModify == false){
                Log.d("쓰레기통 수정 API 통신","error")
                Toast.makeText(context,"다시 시도해 주십시오.",Toast.LENGTH_SHORT).show()
            }
        }

        // 수정 완료 버튼 클릭 시
        binding.adminReportBtn.setOnClickListener {
            if(adminReportViewModel.isSuccess.value == true){
                // 쓰레기통 정보와 위도 경도 보내는 API 통신 필요
                val modifyTrashcan = ModifyTrashcan(adminReportViewModel.reportId.toString(),adminReportViewModel.trashcanId.toString(),adminReportViewModel.latitude.toDouble(),adminReportViewModel.longitude.toDouble(),"0")
                adminReportViewModel.modifyTrashcan(modifyTrashcan)
            }
            else{
                Toast.makeText(context,"위도와 경도를 모두 입력해주세요",Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 사유 창 닫기 버튼 클릭 시
        binding.adminCancelCloseBtn.setOnClickListener {
            adminReportViewModel.isCancelApiSuccess()
            adminReportViewModel.isCancelClicked(false)
        }

        // 취소 사유 API 통신 성공 여부
        adminReportViewModel.isCancelApiSuccess.observe(viewLifecycleOwner){ isCancelApiSuccess ->
            if(isCancelApiSuccess == true){
                adminReportViewModel.isCancelClicked(false)
                Toast.makeText(context,"신고가 취소되었습니다.",Toast.LENGTH_SHORT).show()
            }
            else if(isCancelApiSuccess == false){
                Toast.makeText(context,"다시 시도해 주십시오.",Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 완료 버튼 클릭 시
        binding.adminCancelBtn.setOnClickListener {
            if(adminReportViewModel.isCancelSuccess.value == true){
                // 취소 사유 보내는 API 통신
                Log.d("취소 사유",adminReportViewModel.cancelText)
                val cancelReport = CancelReport(adminReportViewModel.cancelTrashcanId.toString(),adminReportViewModel.cancelReportCategory,adminReportViewModel.cancelText)
                adminReportViewModel.cancelReport(cancelReport)
            }
            else{
                Toast.makeText(context,"취소 사유를 입력해주세요",Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 사유 입력 텍스트 필드
        binding.cancelText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    adminReportViewModel.isCancelSuccess(true)
                }
                else{
                    adminReportViewModel.isCancelSuccess(false)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                adminReportViewModel.cancelText = s.toString()
            }
        })

        // 위도 입력 텍스트 필드
        binding.reportLatitude.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(adminReportViewModel.latitude.isNotEmpty() && adminReportViewModel.longitude.isNotEmpty()){
                    adminReportViewModel.isSuccess(true)
                }
                else{
                    adminReportViewModel.isSuccess(false)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                adminReportViewModel.latitude = s.toString()
            }
        })

        // 경도 입력 텍스트 필드
        binding.reportLongitude.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(adminReportViewModel.latitude.isNotEmpty() && adminReportViewModel.longitude.isNotEmpty()){
                    adminReportViewModel.isSuccess(true)
                }
                else{
                    adminReportViewModel.isSuccess(false)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                adminReportViewModel.longitude = s.toString()
            }
        })

        // 사용자 신고목록 RecyclerView
        adminReportViewModel.adminReportList.observe(viewLifecycleOwner){ adminReportList ->
            if(adminReportList.isEmpty()){
                binding.adminReportRV.visibility = View.GONE
                binding.reportView.visibility = View.VISIBLE
            }
            else{
                binding.adminReportRV.visibility = View.VISIBLE
                binding.reportView.visibility = View.GONE
                binding.adminReportRV.adapter = AdminReportAdapter(adminReportList, adminReportViewModel)
                binding.adminReportRV.layoutManager = LinearLayoutManager(context)
            }
        }

    }
}