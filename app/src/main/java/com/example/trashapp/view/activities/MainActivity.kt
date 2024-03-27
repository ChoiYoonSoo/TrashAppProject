package com.example.trashapp.view.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trashapp.R
import com.example.trashapp.data.ReportItemData
import com.example.trashapp.databinding.ActivityMainBinding
import com.example.trashapp.databinding.ReportListBinding
import com.example.trashapp.network.API
import com.example.trashapp.network.RetrofitInstance
import com.example.trashapp.view.adapter.ReportItemAdapter
import com.example.trashapp.viewmodel.CampViewModel
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}