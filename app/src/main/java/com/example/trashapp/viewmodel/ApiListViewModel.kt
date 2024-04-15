package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.MapData
import com.example.trashapp.data.TmapApiRequest
import com.example.trashapp.network.model.GpsList
import com.example.trashapp.network.model.Place
import com.example.trashapp.network.model.ResultSearchKeyword
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class ApiListViewModel : ViewModel() {

    private val netWorkRepository = NetWorkRepository()

    private val _mapData = MutableLiveData<List<MapData>>()
    val mapData: LiveData<List<MapData>> get() = _mapData

    var selectMapData: MapData? = null

    private val _gpsList = MutableLiveData<GpsList>()
    val gpsList: LiveData<GpsList> get() = _gpsList

    private val _placeList = MutableLiveData<List<Place>>()
    val placeList: LiveData<List<Place>> get() = _placeList

    var selectPlace: MutableLiveData<Place?> = MutableLiveData()

    private val _totalDistance = MutableLiveData<Int>()
    val totalDistance: LiveData<Int> get() = _totalDistance

    private val _totalTime = MutableLiveData<Int>()
    val totalTime: LiveData<Int> get() = _totalTime

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
        Log.d("파라미터 값", paramGpsList.toString())
        try {
            val result = netWorkRepository.getGPS(paramGpsList)
            Log.d("API에서 받아 온 범위의 DB 값", result.toString())
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
            _mapData.postValue(emptyList())
        }
    }

    fun getKaKaoKeyword(key : String, query : String) = viewModelScope.launch {

        try{
            val result = netWorkRepository.searchKeyword(key, query)
            Log.d("카카오 API에서 받아 온 값", result.documents.toString())

            _placeList.postValue(result.documents)
        }catch (e: Exception){
            Log.e("카카오맵 키워드 검색", "실패")
        }

    }

    fun getTmapApi(data: TmapApiRequest) = viewModelScope.launch {
        try {
            val result = netWorkRepository.getTmapApi(data)
            Log.d("Tmap API에서 받아 온 값", "${result.features[0].properties.totalDistance.toString()}, ${result.features[0].properties.totalTime.toString()}")
            _totalDistance.postValue(result.features[0].properties.totalDistance)
            _totalTime.postValue(result.features[0].properties.totalTime)
        } catch (e: Exception) {
            Log.e("Tmap API", e.toString())
        }
    }
}