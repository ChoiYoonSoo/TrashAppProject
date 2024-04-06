package com.example.trashapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.trashapp.data.SignUp
import com.example.trashapp.databinding.FragmentSignUpBinding
import com.example.trashapp.utils.hideKeyboard
import com.example.trashapp.viewmodel.ApiListViewModel
import com.example.trashapp.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel: SignUpViewModel by activityViewModels()

    private var isEmailSuccess : Boolean = false

    private var isNickSuccess = false

    private var isPasswordSuccess = false

    private var isAuthSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpPasswordVisibilityButton1.setOnClickListener {
            if(binding.passwordEditText1.transformationMethod == null) {
                binding.passwordEditText1.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            } else {
                binding.passwordEditText1.transformationMethod = null
            }
        }

        binding.signUpPasswordVisibilityButton2.setOnClickListener {
            if(binding.signUpPasswordEditText2.transformationMethod == null) {
                binding.signUpPasswordEditText2.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            } else {
                binding.signUpPasswordEditText2.transformationMethod = null
            }
        }

        binding.signUpConfirmButton.setOnClickListener {
            binding.signUpAuthTimeText.visibility = View.VISIBLE
        }

        binding.signUpBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.signUpAuthButton.setOnClickListener {
            hideKeyboard()
            if(isAuthSuccess){
                viewModel.duplicateEmailCheck(viewModel.email)

                viewModel.isDuplicateEmail.observe(viewLifecycleOwner) { isDuplicateEmail ->
                    if(isDuplicateEmail) {
                        Log.d("이메일 성공", viewModel.email)
                        isEmailSuccess = true
                        binding.duplicateEmailText.visibility = View.GONE
                    } else {
                        Log.d("이메일 실패", "통신실패")
                        isEmailSuccess = false
                        binding.duplicateEmailText.visibility = View.VISIBLE
                    }
                }
            }
            else{
                Log.d("이메일 유효성 검사 실패","!!")
            }
        }

        binding.signUpDuplicateButton.setOnClickListener {
            viewModel.duplicateNickCheck(viewModel.nickname)
            hideKeyboard()
            viewModel.isDuplicateNick.observe(viewLifecycleOwner) { isDuplicateNick ->
                if(isDuplicateNick) {
                    Log.d("닉네임 성공", viewModel.nickname)
                    isNickSuccess = true
                    binding.duplicateNickText.visibility = View.GONE
                    binding.signUpWarningIcon.visibility = View.GONE
                } else {
                    Log.d("닉네임 실패", "통신실패")
                    isNickSuccess = false
                    binding.duplicateNickText.visibility = View.VISIBLE
                    binding.signUpWarningIcon.visibility = View.VISIBLE
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            hideKeyboard()
            if(isEmailSuccess && isNickSuccess && isPasswordSuccess){
                val signUp = SignUp(viewModel.email, viewModel.password, viewModel.nickname)
                viewModel.signUp(signUp)
            }
            else{
                Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        }

        val emailEditText = binding.signUpEmailEditText
        val passwordEditText = binding.passwordEditText1
        val nicknameEditText = binding.signUpNickEditText


        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isAuthSuccess = false
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.email = s.toString()
                Log.d("email", viewModel.email)

                if(isValidEmail(s.toString())) {
                    isAuthSuccess = true
                }

                if(isValidEmail(s.toString()) || s.toString().isEmpty()) {
                    binding.validateEmailText.visibility = View.GONE
                    binding.duplicateEmailText.visibility = View.GONE
                } else {
                    binding.validateEmailText.visibility = View.VISIBLE
                }
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.password = s.toString()
                Log.d("password", viewModel.password)
                viewModel.validatePassword(s.toString())

                viewModel.isValidatePassword.observe(viewLifecycleOwner) { isValidatePassword ->
                    if(isValidatePassword) {
                        isPasswordSuccess = true
                        binding.signUpPwdIncorrectText.visibility = View.GONE
                    } else {
                        isPasswordSuccess = false
                        binding.signUpPwdIncorrectText.visibility = View.VISIBLE
                    }
                }

            }
        })

        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.nickname = s.toString()
                Log.d("nickname", viewModel.nickname)

                if(isValidNickName(s.toString())) {
                    binding.validateNickText.visibility = View.GONE
                    binding.validateSuccsessNickText.visibility = View.VISIBLE
                } else {
                    binding.validateNickText.visibility = View.VISIBLE
                    binding.validateSuccsessNickText.visibility = View.GONE
                }
            }
        })
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
        return emailRegex.matches(email)
    }

    fun isValidNickName(nickName: String): Boolean {
        val initialConsonants = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
        val trimmedName = nickName.trim()

        // 닉네임 유효성 검사를 위한 정규 표현식: 한글, 영문, 숫자만 허용
        val regex = Regex("^[가-힣a-zA-Z0-9]+$")

        // 닉네임이 공백을 포함하고 있거나, 한 글자이거나, 초성 문자만 있거나, 초성을 포함하는 경우 실패
        if (trimmedName.isEmpty() || trimmedName.length == 1 || trimmedName.any { it.isWhitespace() || it.toString() in initialConsonants }) {
            return false
        }

        // 닉네임이 한글, 영문, 숫자만 포함하는지 검사
        if (!regex.matches(trimmedName)) {
            return false
        }

        return true
    }
    private fun Fragment.hideKeyboard() {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}