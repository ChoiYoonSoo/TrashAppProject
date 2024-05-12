package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.network.model.ModifyTrashcan
import com.example.trashapp.network.model.ReportTrashCan
import com.example.trashapp.network.model.UserReportList
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class AdminReportViewModel : ViewModel(){

    private val netWorkRepository = NetWorkRepository()

    private val _adminReportList = MutableLiveData<List<UserReportList>>()
    val adminReportList: LiveData<List<UserReportList>> get() = _adminReportList

    private val _isItemClicked = MutableLiveData<Boolean>()
    val isItemClicked: LiveData<Boolean> get() = _isItemClicked

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _isModify = MutableLiveData<Boolean?>()
    val isModify: LiveData<Boolean?> get() = _isModify

    var latitude : String = ""
    var longitude : String = ""
    var trashcanId : Long = 0
    var userId : Long = 0
    var reportId : Long = 0

    fun findAllReportTrashcan() = viewModelScope.launch {
        try {
            val result = netWorkRepository.findAllReportTrashcan()
            _adminReportList.postValue(result)
            Log.d("모든 유저 신고 조회 성공", result.toString())
        } catch (e: Exception) {
            Log.d("모든 유저 신고 조회 실패","error : ${e.message}")
        }
    }

    fun deleteTrashcan(reportTrashCan : ReportTrashCan) = viewModelScope.launch {
        try{
            val result = netWorkRepository.deleteTrashcan(reportTrashCan)
            Log.d("모든 유저 신고 삭제 성공", result.toString())
            findAllReportTrashcan()
        } catch (e: Exception) {
            Log.d("모든 유저 신고 삭제 실패","error : ${e.message}")
        }
    }

    fun modifyTrashcan(modifyTrashcan: ModifyTrashcan) = viewModelScope.launch {
        try {
            _isModify.postValue(true)
            val result = netWorkRepository.modifyTrashcan(modifyTrashcan)
            Log.d("모든 유저 신고 수정 성공", result.toString())
            findAllReportTrashcan()
        } catch (e: Exception) {
            _isModify.postValue(false)
            Log.d("모든 유저 신고 수정 실패","error : ${e.message}")
        }
    }

    fun itemClicked(value : Boolean) {
        _isItemClicked.postValue(value)
    }

    fun isSuccess(value : Boolean) {
        _isSuccess.postValue(value)
    }

    fun isModify() {
        _isModify.postValue(null)
    }
}