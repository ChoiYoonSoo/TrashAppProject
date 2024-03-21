package com.example.trashapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentSettingBinding
import com.google.android.material.navigation.NavigationBarItemView

class SettingFragment : Fragment() {
    private lateinit var binding : FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingBackButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.emailButton.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_emailFragment)
        }

        binding.privacyButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_personalInfoFragment)
        }

        binding.contactButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_contactFragment)
        }
    }

}