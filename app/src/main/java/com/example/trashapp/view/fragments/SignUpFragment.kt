package com.example.trashapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trashapp.R
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

        val authTimeText = binding.authTimeText
        authTimeText.visibility = View.GONE

        val pwdIncorrectText = binding.pwdIncorrectText
        pwdIncorrectText.visibility = View.GONE

        val duplicateNickText = binding.duplicateNickText
        duplicateNickText.visibility = View.GONE

        binding.confirmButton.setOnClickListener {
            authTimeText.visibility = View.VISIBLE
        }

        binding.backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

    }

}