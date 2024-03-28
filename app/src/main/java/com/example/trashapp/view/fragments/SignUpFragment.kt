package com.example.trashapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trashapp.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authTimeText = binding.signUpAuthTimeText
        authTimeText.visibility = View.GONE

        val pwdIncorrectText = binding.signUpPwdIncorrectText
        pwdIncorrectText.visibility = View.GONE

        val duplicateNickText = binding.signUpDuplicateNickText
        duplicateNickText.visibility = View.GONE

        binding.signUpConfirmButton.setOnClickListener {
            authTimeText.visibility = View.VISIBLE
        }

        binding.signUpBackButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

    }

}