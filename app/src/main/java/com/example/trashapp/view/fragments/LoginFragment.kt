package com.example.trashapp.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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

    private val viewModel: LoginViewModel by activityViewModels()

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

        // UserTokenViewModel 의존성 주입
        val userRepository =
            UserTokenRepository(requireContext())
        val factory = UserTokenViewModelFactory(userRepository)
        userTokenViewModel = ViewModelProvider(this, factory).get(UserTokenViewModel::class.java)

        // 뒤로가기 버튼
        binding.loginBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 회원가입 버튼
        binding.loginSignUpBtn.setOnClickListener {
            binding.loginEmailText.setText("")
            binding.loginPasswordText.setText("")
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment2_to_signUpFragment)
        }

        viewModel.token.observe(viewLifecycleOwner) { token ->
            // token 값이 변경되면, 그 값이 null이 아닐 때 SharedPreferences에 저장
            token?.let {
                userTokenViewModel.saveToken(it)
                Log.d("login token : ", userTokenViewModel.getToken().toString())
                Navigation.findNavController(view)
                    .navigate(R.id.action_loginFragment2_to_mapFragment)

            } ?: run {
                Toast.makeText(context, "아이디와 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoginSuccess.observe(viewLifecycleOwner) { isLoginSuccess ->
            viewModel.isPasswordSuccess.observe(viewLifecycleOwner) { isPasswordSuccess ->
               binding.loginBtn.isEnabled = isLoginSuccess && isPasswordSuccess
            }
        }

        // 로그인 버튼 및 유저 토큰값 SharedPreferences에 저장
        binding.loginBtn.setOnClickListener {
            Log.d("login email : ", viewModel.email)
            Log.d("login password : ", viewModel.password)

            val login = Login(viewModel.email, viewModel.password)
            viewModel.login(login)
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
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("afterTextChanged email : ", s.toString())
                if (isValidEmail(s.toString()) && s.toString().isNotEmpty()) {
                    viewModel.isLoginSuccess(true)
                    viewModel.email = s.toString()
                    binding.validateEmailErrorText.visibility = View.GONE
                } else if (s.toString().isEmpty()) {
                    viewModel.isLoginSuccess(false)
                    binding.validateEmailErrorText.visibility = View.GONE
                } else {
                    viewModel.isLoginSuccess(false)
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
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("afterTextChanged password : ", s.toString())
                if (s.toString().isNotEmpty()) {
                    viewModel.isPasswordSuccess(true)
                    viewModel.password = s.toString()
                } else {
                    viewModel.isPasswordSuccess(false)
                }
            }
        })
    }
        // 이메일 유효성 검사
        fun isValidEmail(email: String): Boolean {
            val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
            return emailRegex.matches(email)
        }
    }