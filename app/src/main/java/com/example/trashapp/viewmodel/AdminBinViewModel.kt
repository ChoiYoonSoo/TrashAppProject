package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.network.model.UnknownTrashcan
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class AdminBinViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _adminBinList = MutableLiveData<List<UnknownTrashcan>>()
    val adminBinList: LiveData<List<UnknownTrashcan>> get() = _adminBinList

    private val _isItemClicked = MutableLiveData<Boolean>()
    val isItemClicked: LiveData<Boolean> get() = _isItemClicked

    var trachcanId = ""
    var userId = ""
    var location = ""
    var image = ""

    fun findAllUnknownTrashcans() = viewModelScope.launch {
        try {
            val result = netWorkRepository.findAllUnknownTrashcans()
            _adminBinList.postValue(result)
            Log.d("사용자 쓰레기통 등록 조회 성공", result.toString())
        } catch (e: Exception) {
            Log.d("사용자 쓰레기통 등록 조회 실패",e.toString())
        }
    }

    fun deleteAdminUnknownList(deleteAdminUnknownList: UnknownTrashcan) = viewModelScope.launch {
        try {
            val result = netWorkRepository.deleteUnknownTrashcan(deleteAdminUnknownList.unknown_trashcan_id)
            findAllUnknownTrashcans()
            Log.d("사용자 쓰레기통 등록 삭제 성공", deleteAdminUnknownList.toString())
            Log.d("삭제된 후 리스트", result.toString())
        } catch (e: Exception) {
            Log.d("사용자 쓰레기통 등록 삭제 실패",e.toString())
        }
    }

    fun itemClicked(value : Boolean) {
        _isItemClicked.postValue(value)
    }
}