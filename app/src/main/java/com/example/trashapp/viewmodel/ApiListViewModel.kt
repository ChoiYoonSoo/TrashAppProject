package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.ApiValue
import com.example.trashapp.data.MapData
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class ApiListViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _mapData = MutableLiveData<List<MapData>>()
    val mapData: LiveData<List<MapData>> get() = _mapData

    private val _selectMapData = MutableLiveData<MapData>()
    val selectMapData: LiveData<MapData> get() = _selectMapData

    fun getApiList() = viewModelScope.launch {

        val result = netWorkRepository.getApiTest()

        try {
            // 위도 경도 추가
            val newMapData : List<MapData> = result.map {data ->
                MapData(
                    data.latitude.toString(),
                    data.longitude.toString(),
                    data.latitude,
                    data.longitude
                )
            }
            _mapData.postValue(newMapData)
            _selectMapData.postValue(newMapData[0])
        } catch (e: Exception) {
            Log.e("CampViewModel", "Error fetching campsite list", e)
        }
    }

}