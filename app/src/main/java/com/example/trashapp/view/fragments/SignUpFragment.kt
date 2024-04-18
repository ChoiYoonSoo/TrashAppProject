package com.example.trashapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.trashapp.R
import com.example.trashapp.data.SignUp
import com.example.trashapp.databinding.FragmentSignUpBinding
import com.example.trashapp.network.model.EmailAuth
import com.example.trashapp.viewmodel.EmailAuthViewModel
import com.example.trashapp.viewmodel.SignUpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel: SignUpViewModel by activityViewModels()

    private val emailAuthViewModel: EmailAuthViewModel by activityViewModels()

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인증번호 버튼 비활성화
        binding.signUpConfirmBtn.isEnabled = false

        // 뒤로 가기 버튼 클릭 시
        binding.signUpBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 비밀번호 재확인 입력 비활성화
        binding.passwordEditText2.isEnabled = false

        // 닉네임 중복 검사 비활성화
        binding.signUpDuplicateBtn.isEnabled = false

        // 인증번호 확인 버튼 클릭 시
        binding.signUpConfirmBtn.setOnClickListener {
            val emailAuth = EmailAuth(viewModel.afterEmail, emailAuthViewModel.authNumber)
            emailAuthViewModel.getEmailAuth(emailAuth)
        }

        // 첫 번째 비밀번호 입력란의 비밀번호 가시성 버튼 클릭 시
        binding.signUpPwdVisibilityBtn1.setOnClickListener {
            if (binding.passwordEditText1.transformationMethod == null) {
                binding.passwordEditText1.transformationMethod =
                    android.text.method.PasswordTransformationMethod.getInstance()
            } else {
                binding.passwordEditText1.transformationMethod = null
            }
        }

        // 두 번째 비밀번호 입력란의 비밀번호 가시성 버튼 클릭 시
        binding.signUpPwdVisibilityBtn2.setOnClickListener {
            if (binding.passwordEditText2.transformationMethod == null) {
                binding.passwordEditText2.transformationMethod =
                    android.text.method.PasswordTransformationMethod.getInstance()
            } else {
                binding.passwordEditText2.transformationMethod = null
            }
        }

        // 이메일 인증번호 받기 버튼 클릭 시
        binding.signUpAuthBtn.setOnClickListener {
            hideKeyboard()
            if (viewModel.isSignUpSuccess.value == true) {
                viewModel.afterEmail = viewModel.email
                viewModel.duplicateEmailCheck(viewModel.email)
            } else {
                Log.d("버튼 클릭", "회원 가입 성공 상태가 아님")
            }
        }

        // 이메일 중복 확인
        viewModel.isDuplicateEmail.observe(viewLifecycleOwner) { isDuplicateEmail ->
            if (isDuplicateEmail) {
                Log.d("이메일 중복 검사 성공 ", viewModel.email)
                if(timer != null){
                    timer?.cancel()
                    timer = null
                }
                startTimer(180000)
                viewModel.isEmailSuccess(true)
                binding.duplicateEmailText.visibility = View.GONE
                binding.signUpAuthTimeText.visibility = View.VISIBLE
                binding.signUpAuthTimeText2.visibility = View.GONE
                binding.signUpConfirmBtn.isEnabled = true
            } else {
                Log.d("이메일 중복 검사 ", "실패")
                viewModel.isEmailSuccess(false)
                binding.duplicateEmailText.visibility = View.VISIBLE
                binding.signUpAuthTimeText.visibility = View.GONE
            }
        }

        // 닉네임 중복 확인 버튼 클릭 시
        binding.signUpDuplicateBtn.setOnClickListener {
            viewModel.duplicateNickCheck(viewModel.nickname)
            hideKeyboard()
            viewModel.isDuplicateNick.observe(viewLifecycleOwner) { isDuplicateNick ->
                if (isDuplicateNick) {
                    Log.d("닉네임 중복 검사 성공 ", viewModel.nickname)
                    viewModel.isNickSuccess(true)
                    binding.duplicateNickText.visibility = View.GONE
                    binding.signUpWarningIcon.visibility = View.GONE
                    binding.validateSuccsessNickText.visibility = View.VISIBLE
                } else {
                    Log.d("닉네임 중복 검사 ", "실패")
                    viewModel.isNickSuccess(false)
                    binding.duplicateNickText.visibility = View.VISIBLE
                    binding.signUpWarningIcon.visibility = View.VISIBLE
                    binding.validateSuccsessNickText.visibility = View.GONE
                }
            }
        }

        // 회원가입 버튼 활성화 코드
        viewModel.isEmailSuccess.observe(viewLifecycleOwner) { isEmailSuccess ->
            viewModel.isNickSuccess.observe(viewLifecycleOwner) { isNickSuccess ->
                viewModel.isPasswordSuccess.observe(viewLifecycleOwner) { isPasswordSuccess ->
                    viewModel.isAgree.observe(viewLifecycleOwner) { isAgree ->
                        viewModel.isSignUpSuccess.observe(viewLifecycleOwner){ isSignUpSuccess ->
                            binding.signUpButton.isEnabled = isEmailSuccess && isNickSuccess && isPasswordSuccess && isAgree && isSignUpSuccess
                        }
                    }
                }
            }
        }

        // 회원가입 버튼 클릭 시
        binding.signUpButton.setOnClickListener {
            if(viewModel.afterEmail != viewModel.email){
                Toast.makeText(context, "이메일 인증을 완료해주세요", Toast.LENGTH_SHORT).show()
            }else{
                hideKeyboard()
                val signUp = SignUp(viewModel.email, viewModel.password, viewModel.nickname)
                lifecycleScope.launch {
                    viewModel.signUp(signUp)
                    delay(500)
                }
                parentFragmentManager.popBackStack()
            }
        }

        // 개인정보 처리방침 클릭 시
        binding.signUpContainer4.setOnClickListener {
            viewModel.agree = !viewModel.agree
            if(viewModel.agree){
                viewModel.isAgree(true)
                binding.signUpAcceptBtn.setImageResource(R.drawable.checkcircleselected)
            }
            else{
                viewModel.isAgree(false)
                binding.signUpAcceptBtn.setImageResource(R.drawable.checkcircle)
            }
        }

        binding.signUpAcceptBtn.setOnClickListener {
            viewModel.agree = !viewModel.agree
            if(viewModel.agree){
                viewModel.isAgree(true)
                binding.signUpAcceptBtn.setImageResource(R.drawable.checkcircleselected)
            }
            else{
                viewModel.isAgree(false)
                binding.signUpAcceptBtn.setImageResource(R.drawable.checkcircle)
            }
        }

        val emailEditText = binding.signUpEmailEditText
        val passwordEditText = binding.passwordEditText1
        val passwordEditText2 = binding.passwordEditText2
        val nicknameEditText = binding.signUpNickEditText
        val signUpAuthText = binding.signUpAuthText


        // 이메일 텍스트 필드 이벤트 처리
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signUpAuthTimeText.visibility = View.GONE
                binding.signUpConfirmBtn.isEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.email = s.toString()
                Log.d("afterTextChanged email : ", viewModel.email)

                if (isValidEmail(s.toString())) {
                    viewModel.isSignUpSuccess(true)
                }
                else{
                    viewModel.isSignUpSuccess(false)
                }

                if (isValidEmail(s.toString()) || s.toString().isEmpty()) {
                    binding.validateEmailText.visibility = View.GONE
                    binding.duplicateEmailText.visibility = View.GONE
                } else {
                    binding.validateEmailText.visibility = View.VISIBLE
                }
            }
        })

        // 비밀번호 텍스트 필드 이벤트 처리
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(viewModel.afterPassword != s.toString()){
                    viewModel.isPasswordSuccess(false)
                    binding.signUpPwdIncorrectText2.visibility = View.VISIBLE
                }else{
                    viewModel.isPasswordSuccess(true)
                    binding.signUpPwdIncorrectText2.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty()){
                    binding.signUpPwdIncorrectText.visibility = View.GONE
                }else{
                    viewModel.password = s.toString()
                    Log.d("afterTextChanged password : ", viewModel.password)
                    viewModel.validatePassword(s.toString())

                    viewModel.isValidatePassword.observe(viewLifecycleOwner) { isValidatePassword ->
                        if (isValidatePassword) {
                            binding.signUpPwdIncorrectText.visibility = View.GONE
                            binding.passwordEditText2.isEnabled = true
                        } else {
                            binding.signUpPwdIncorrectText2.visibility = View.GONE
                            binding.passwordEditText2.isEnabled = false
                            binding.signUpPwdIncorrectText.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })

        // 비밀번호 확인 텍스트 필드 이벤트 처리
        passwordEditText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.afterPassword = s.toString()
                if (viewModel.password == s.toString()) {
                    viewModel.isPasswordSuccess(true)
                    binding.signUpPwdIncorrectText2.visibility = View.GONE

                } else {
                    viewModel.isPasswordSuccess(false)
                    binding.signUpPwdIncorrectText2.visibility = View.VISIBLE
                }
            }
        })

        // 닉네임 텍스트 필드 이벤트 처리
        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.isNickSuccess(false)
                binding.validateSuccsessNickText.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty()){
                    binding.validateNickText.visibility = View.VISIBLE
                    binding.validateSuccsessNickText.visibility = View.GONE
                    viewModel.isNickSuccess(false)
                }
                else{
                    viewModel.nickname = s.toString()
                    Log.d("afterTextChanged nickname : ", viewModel.nickname)

                    if (isValidNickName(s.toString())) {
                        binding.signUpDuplicateBtn.isEnabled = true
                        binding.validateNickText.visibility = View.GONE
                    } else {
                        viewModel.isNickSuccess(false)
                        binding.signUpDuplicateBtn.isEnabled = false
                        binding.validateNickText.visibility = View.VISIBLE
                        binding.validateSuccsessNickText.visibility = View.GONE
                    }
                }
            }
        })

        // 이메일 인증번호 텍스트 필드 이벤트 처리
        signUpAuthText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailAuthViewModel.authNumber = s.toString()
                Log.d("afterTextChanged authNumber : ", emailAuthViewModel.authNumber)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    // 3분 타이머
    private fun startTimer(timeInMillis: Long){

        timer = object : CountDownTimer(timeInMillis, 1000) { // 1000ms = 1초 간격
            override fun onTick(millisUntilFinished: Long) {
                // 남은 시간을 분:초 형태로 변환하여 텍스트 뷰에 표시
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                Log.d("타이머","$minutes:$seconds")
                binding.signUpAuthTimeText.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // 타이머가 끝나면 00:00 표시
                binding.signUpAuthTimeText.visibility = View.GONE
                binding.signUpConfirmBtn.isEnabled = false
                binding.signUpAuthTimeText2.visibility = View.VISIBLE
            }
        }.start()
    }

    // 이메일 유효성 검사
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
        return emailRegex.matches(email)
    }

    // 닉네임 유효성 검사
    fun isValidNickName(nickName: String): Boolean {
        val initialConsonants = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
        val trimmedName = nickName.trim()

        // 닉네임 유효성 검사를 위한 정규 표현식: 한글, 영문, 숫자만 허용
        val regex = Regex("^[가-힣a-zA-Z0-9]+$")

        // 닉네임이 공백을 포함하고 있거나, 한 글자 이거나, 초성 문자만 있거나, 초성을 포함하는 경우 실패
        if (trimmedName.isEmpty() || trimmedName.length == 1 || trimmedName.any { it.isWhitespace() || it.toString() in initialConsonants }) {
            return false
        }

        // 닉네임이 한글, 영문, 숫자만 포함하는지 검사
        if (!regex.matches(trimmedName)) {
            return false
        }
        return true
    }

    // 키보드 숨기기
    private fun Fragment.hideKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}