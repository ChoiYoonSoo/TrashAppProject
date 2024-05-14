package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.network.model.MyReportList
import com.example.trashapp.network.model.ReportTrashCan
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class MyReportViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _myReportList = MutableLiveData<List<MyReportList>>()
    val myReportList: LiveData<List<MyReportList>> get() = _myReportList

    var reportError = ""

    private val _isReportSuccess = MutableLiveData<Boolean?>()
    val isReportSuccess: LiveData<Boolean?> get() = _isReportSuccess

    fun myReportTrashcans(token: String) = viewModelScope.launch {
        try {
            val result = netWorkRepository.myReportTrashcans(token)
            _myReportList.postValue(result)
            Log.d("나의 신고 내역 조회 성공", result.toString())
        } catch (e: Exception) {
            Log.d("나의 신고 내역 조회 실패",e.printStackTrace().toString())
        }
    }

    fun deleteReportTrashcan(reportTrashCan: ReportTrashCan, token: String) = viewModelScope.launch {
        try {
            val result = netWorkRepository.deleteReportTrashcan(reportTrashCan, token)
            myReportTrashcans(token)

            if(result.isSuccessful){
                reportError = result.body().toString()
            }
            else{
                reportError = result.errorBody().toString()
            }
            _isReportSuccess.postValue(true)
            Log.d("나의 신고 내역 삭제 성공", reportTrashCan.toString())
        } catch (e: Exception) {
            _isReportSuccess.postValue(false)
            Log.d("나의 신고 내역 삭제 실패",e.printStackTrace().toString())
        }
    }

    fun isReportSuccessFalse(){
        _isReportSuccess.postValue(null)
    }
}