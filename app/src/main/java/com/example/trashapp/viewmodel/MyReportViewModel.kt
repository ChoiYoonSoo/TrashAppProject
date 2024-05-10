package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.network.model.MyReportList
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class MyReportViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _myReportList = MutableLiveData<List<MyReportList>>()
    val myReportList: LiveData<List<MyReportList>> get() = _myReportList

    fun myReportTrashcans(token: String) = viewModelScope.launch {
        try {
            val result = netWorkRepository.myReportTrashcans(token)
            _myReportList.postValue(result)
            Log.d("내가 신고한 쓰레기통 조회 API 통신 성공", result.toString())
        } catch (e: Exception) {
            Log.d("내가 신고한 쓰레기통 조회 API 통신 실패",e.printStackTrace().toString())
        }
    }
}