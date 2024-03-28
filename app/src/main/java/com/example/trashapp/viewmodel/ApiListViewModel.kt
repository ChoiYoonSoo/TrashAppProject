package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.MapData
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class ApiListViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _mapData = MutableLiveData<List<MapData>>()
    val mapData: LiveData<List<MapData>> get() = _mapData

    fun getApiList() = viewModelScope.launch {

        val result = netWorkRepository.getApiTest()

        try {
            val apiList = result.data

            // 위도 경도 추가
            val newMapData = apiList.map {
                MapData(
                    it.district,
                    it.address,
                    it.latitude.toDoubleOrNull()?: 0.0,
                    it.longitude.toDoubleOrNull() ?: 0.0
                )
            }
            _mapData.postValue(newMapData)
        } catch (e: Exception) {
            Log.e("CampViewModel", "Error fetching campsite list", e)
        }
    }

}