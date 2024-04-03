package com.example.trashapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentIntroBinding
import com.example.trashapp.network.model.GpsList
import com.example.trashapp.viewmodel.ApiListViewModel

class IntroFragment : Fragment() {

    private lateinit var binding: FragmentIntroBinding

    private val viewModel : ApiListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startWithoutLogin.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_introFragment_to_mapFragment)
        }

        binding.introLoginBtn.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_introFragment_to_loginFragment2)
        }
    }
}