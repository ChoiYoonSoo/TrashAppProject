package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.MapData
import com.example.trashapp.network.model.GpsList
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class ApiListViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _mapData = MutableLiveData<List<MapData>>()
    val mapData: LiveData<List<MapData>> get() = _mapData

    var selectMapData: MapData? = null

    private val _gpsList = MutableLiveData<GpsList>()
    val gpsList: LiveData<GpsList> get() = _gpsList

    fun getApiList() = viewModelScope.launch {

        val result = netWorkRepository.getApiTest()

        try {
            // 위도 경도 추가
            val newMapData: List<MapData> = result.map { data ->
                MapData(
                    data.detailAddress,
                    data.address,
                    data.latitude,
                    data.longitude,
                    data.roadviewImgpath,
                    data.id
                )
            }
            _mapData.postValue(newMapData)
        } catch (e: Exception) {
            Log.e("CampViewModel", "Error fetching campsite list", e)
        }
    }

    fun getGpsList(paramGpsList: GpsList) = viewModelScope.launch {
        _mapData.postValue(emptyList())
        Log.d("파라미터 값", paramGpsList.toString())
        val result = netWorkRepository.getGPS(paramGpsList)
        Log.d("API에서 받아 온 범위의 DB 값", result.toString())

        try {
            // 위도 경도 추가
            val newMapData: List<MapData> = result.map { data ->
                MapData(
                    data.detailAddress,
                    data.address,
                    data.latitude,
                    data.longitude,
                    data.roadviewImgpath,
                    data.id
                )
            }
            _mapData.postValue(newMapData)
        } catch (e: Exception) {
            Log.e("CampViewModel", "Error fetching campsite list", e)
        }
    }
}