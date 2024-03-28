package com.example.trashapp.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.trashapp.databinding.ActivityMainBinding
import com.example.trashapp.viewmodel.CampViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel : CampViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCampsiteList()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}