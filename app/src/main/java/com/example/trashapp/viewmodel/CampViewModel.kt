package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.MapData
import com.example.trashapp.network.model.Item
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class CampViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _mapData = MutableLiveData<List<MapData>>()
    val mapData: LiveData<List<MapData>> get() = _mapData

    fun getCampsiteList() = viewModelScope.launch {

        val result = netWorkRepository.getCampList()

        try {
            val campsiteList = result.response.body.items.item

            // 위도 경도 추가
            val newMapData = campsiteList.map {
                MapData(
                    it.facltNm,
                    it.addr1,
                    it.mapY.toDoubleOrNull() ?: 0.0,
                    it.mapX.toDoubleOrNull() ?: 0.0
                )
            }
            _mapData.postValue(newMapData)
        } catch (e: Exception) {
            Log.e("CampViewModel", "Error fetching campsite list", e)
        }
    }

}