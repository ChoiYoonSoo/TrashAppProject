package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Outline
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentSettingBinding
import com.example.trashapp.factory.UserTokenViewModelFactory
import com.example.trashapp.repository.UserTokenRepository
import com.example.trashapp.view.SharedPreferencesManager
import com.example.trashapp.view.activities.MainActivity
import com.example.trashapp.viewmodel.AdminBinViewModel
import com.example.trashapp.viewmodel.AdminReportViewModel
import com.example.trashapp.viewmodel.MyReportViewModel
import com.example.trashapp.viewmodel.UserInfoViewModel
import com.example.trashapp.viewmodel.UserTokenViewModel

class SettingFragment : Fragment() {
    private lateinit var binding : FragmentSettingBinding

    private val userInfoViewModel : UserInfoViewModel by activityViewModels()

    private lateinit var userTokenViewModel: UserTokenViewModel

    private val adminBinViewModel : AdminBinViewModel by activityViewModels()

    private val adminReportViewModel : AdminReportViewModel by activityViewModels()

    private val myReportViewModel : MyReportViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAnim()

        // UserTokenViewModel 의존성 주입
        val userRepository = UserTokenRepository(requireContext()) // UserTokenRepository 인스턴스를 생성하거나 의존성 주입을 통해 제공받습니다.
        val factory = UserTokenViewModelFactory(userRepository)
        userTokenViewModel = ViewModelProvider(this, factory).get(UserTokenViewModel::class.java)

        if(userTokenViewModel.getToken() != null){
            val userToken = userTokenViewModel.getToken().toString()
            userInfoViewModel.getUserInfo(userToken)
            binding.settingLoginOrout.text = getString(R.string.logoutText)
        }else{
            binding.settingLoginOrout.text = getString(R.string.loginText)
            binding.settingEditMyInfo.visibility = View.GONE
            Log.d("유저 토큰 ","없음")
        }

        // 유저 정보가 들어오면 텍스트 변동
        userInfoViewModel.email.observe(viewLifecycleOwner) { email ->
            binding.settingEmail.text = email
        }
        userInfoViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.settingNickName.text = nickname
        }
        userInfoViewModel.roleType.observe(viewLifecycleOwner) { roleType ->
            if(roleType == "admin"){
                binding.settingReportButton.visibility = View.VISIBLE
                binding.settingMyReportButton.visibility = View.GONE
            }
            else{
                binding.settingReportButton.visibility = View.GONE
                binding.settingMyReportButton.visibility = View.VISIBLE
            }
        }

        // 로그인 or 로그아웃 클릭 시
        binding.settingLoginOrout.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        // 쓰레기통 관리 버튼 클릭 시
        binding.settingBinButton.setOnClickListener {
            adminBinViewModel.findAllUnknownTrashcans()
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_adminReportFragment)
        }

        // 사용자 신고 목록 버튼 클릭 시
        binding.settingReportButton.setOnClickListener {
            adminReportViewModel.findAllReportTrashcan()
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_adminReportFragment2)
        }

        // 나의 신고 내역 버튼 클릭 시
        binding.settingMyReportButton.setOnClickListener {
            myReportViewModel.myReportTrashcans(SharedPreferencesManager.getToken(requireContext())!!)
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_myReportFragment)
        }

        // 뒤로가기 버튼
        binding.settingBackButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        // 탈퇴하기 버튼
        binding.settingUnSubButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_unsubFragment)
        }

        binding.settingEmailButton.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_emailFragment)
        }

        binding.settingPrivacyButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_personalInfoFragment)
        }

        binding.settingContactButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_contactFragment)
        }

        // 프로필 이미지 원형 변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val imageView = view.findViewById<ImageView>(R.id.settingProfIleImage)
            imageView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    // ImageView의 크기에 맞는 원형 outline 설정
                    outline?.setOval(0, 0, view!!.width, view.height)
                }
            }
            imageView.clipToOutline = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonAnim() {
        // 애니메이션
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)

        // 뒤로가기 버튼 애니메이션
        binding.settingBackButton.setOnTouchListener { v, event ->
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

        // 로그인 or 로그아웃 버튼 애니메이션
        binding.settingLoginOrout.setOnTouchListener{
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

        // 쓰레기통 관리 버튼 애니메이션
        binding.settingBinButton.setOnTouchListener{
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

        // 사용자 신고 목록 버튼 애니메이션
        binding.settingReportButton.setOnTouchListener{
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

        // 내 신고내역 버튼 애니메이션
        binding.settingMyReportButton.setOnTouchListener{
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

        // 탈퇴하기 버튼 애니메이션
        binding.settingUnSubButton.setOnTouchListener{
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