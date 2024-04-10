package com.example.trashapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentIntroBinding
import com.example.trashapp.factory.UserTokenViewModelFactory
import com.example.trashapp.repository.UserTokenRepository
import com.example.trashapp.viewmodel.UserTokenViewModel

class IntroFragment : Fragment() {

    private lateinit var binding: FragmentIntroBinding

    private lateinit var userTokenViewModel: UserTokenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UserTokenViewModel 의존성 주입
        val userRepository = UserTokenRepository(requireContext()) // UserTokenRepository 인스턴스를 생성하거나 의존성 주입을 통해 제공받습니다.
        val factory = UserTokenViewModelFactory(userRepository)
        userTokenViewModel = ViewModelProvider(this, factory).get(UserTokenViewModel::class.java)

        userTokenViewModel.removeToken()

        binding.startWithoutLogin.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_introFragment_to_mapFragment)
        }

        binding.introLoginBtn.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_introFragment_to_loginFragment2)
        }
    }
}