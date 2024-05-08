package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class AdminReportViewModel : ViewModel(){
    private val netWorkRepository = NetWorkRepository()

    fun findAllReportTrashcan() = viewModelScope.launch {
        try {
            val result = netWorkRepository.findAllReportTrashcan()
            Log.d("모든 유저 신고 조회 API 통신 성공", result.toString())
        } catch (e: Exception) {
            Log.d("모든 유저 신고 조회 API 통신","error : ${e.message}")
        }
    }
}