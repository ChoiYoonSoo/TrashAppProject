package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.trashapp.R
import com.example.trashapp.data.Login
import com.example.trashapp.databinding.FragmentLoginBinding
import com.example.trashapp.factory.UserTokenViewModelFactory
import com.example.trashapp.repository.UserTokenRepository
import com.example.trashapp.viewmodel.LoginViewModel
import com.example.trashapp.viewmodel.UserTokenViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var userTokenViewModel: UserTokenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAnim()

        // UserTokenViewModel 의존성 주입
        val userRepository =
            UserTokenRepository(requireContext())
        val factory = UserTokenViewModelFactory(userRepository)
        userTokenViewModel = ViewModelProvider(this, factory).get(UserTokenViewModel::class.java)

        // 뒤로가기 버튼
        binding.loginBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
            loginViewModel.resetClear()
            loginViewModel.tokenClear()
        }

        // 회원가입 버튼
        binding.loginSignUpBtn.setOnClickListener {
            binding.loginEmailText.setText("")
            binding.loginPasswordText.setText("")
            loginViewModel.tokenClear()
            loginViewModel.resetClear()
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment2_to_signUpFragment)
        }

        // 로그인 성공하여 토큰값이 변경되었을 때
        loginViewModel.isTokenSuccess.observe(viewLifecycleOwner) { isTokenSuccess ->
            binding.loginProgressBar.visibility = View.GONE
            // token 값이 변경되면, 그 값이 빈 문자열이 아닐 때 SharedPreferences에 저장
            if (isTokenSuccess == true) {
                loginViewModel.resetClear()
                loginViewModel.tokenClear()
                userTokenViewModel.saveToken(loginViewModel.token)
                Log.d("login token : ", userTokenViewModel.getToken().toString())
                Navigation.findNavController(view)
                    .navigate(R.id.action_loginFragment2_to_mapFragment)
            } else if(isTokenSuccess == false) {
                Toast.makeText(context, "아이디 또는 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 로그인 조건에 부합할 경우
        loginViewModel.isLoginSuccess.observe(viewLifecycleOwner) { isLoginSuccess ->
            loginViewModel.isPasswordSuccess.observe(viewLifecycleOwner) { isPasswordSuccess ->
               binding.loginBtn.isEnabled = isLoginSuccess == true && isPasswordSuccess == true
            }
        }

        // 로그인 버튼 및 유저 토큰값 SharedPreferences에 저장
        binding.loginBtn.setOnClickListener {
            binding.loginProgressBar.visibility = View.VISIBLE
            Log.d("login email : ", loginViewModel.email)
            Log.d("login password : ", loginViewModel.password)

            val login = Login(loginViewModel.email, loginViewModel.password)
            loginViewModel.login(login)
            hideKeyboard()
        }

        // 이메일 텍스트필드
        binding.loginEmailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(loginViewModel.password.isNotEmpty()) {
                    loginViewModel.isPasswordSuccess(true)
                }else{
                    loginViewModel.isPasswordSuccess(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("afterTextChanged email : ", s.toString())
                if (isValidEmail(s.toString()) && s.toString().isNotEmpty()) {
                    loginViewModel.isLoginSuccess(true)
                    loginViewModel.email = s.toString()
                    binding.validateEmailErrorText.visibility = View.GONE
                } else if (s.toString().isEmpty()) {
                    loginViewModel.isLoginSuccess(false)
                    binding.validateEmailErrorText.visibility = View.GONE
                } else {
                    loginViewModel.isLoginSuccess(false)
                    binding.validateEmailErrorText.visibility = View.VISIBLE
                }
            }
        })

        // 비밀번호 텍스트필드
        binding.loginPasswordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(loginViewModel.email.isNotEmpty()){
                    loginViewModel.isLoginSuccess(true)
                }
                else{
                    loginViewModel.isLoginSuccess(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("afterTextChanged password : ", s.toString())
                if (s.toString().isNotEmpty()) {
                    loginViewModel.isPasswordSuccess(true)
                    loginViewModel.password = s.toString()
                } else {
                    loginViewModel.isPasswordSuccess(false)
                }
            }
        })
    }

    // 이메일 유효성 검사
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
        return emailRegex.matches(email)
    }

    // 키보드 숨기기
    private fun Fragment.hideKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonAnim(){
        // 애니메이션
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)

        binding.loginBtn.setOnTouchListener{ v, event ->
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

        binding.loginSignUpBtn.setOnTouchListener{ v, event ->
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

        binding.loginBackButton.setOnTouchListener{ v, event ->
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