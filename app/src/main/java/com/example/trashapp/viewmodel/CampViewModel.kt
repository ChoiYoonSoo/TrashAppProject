package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.repository.NetWorkRepository
import kotlinx.coroutines.launch

class CampViewModel : ViewModel() {
    private val netWorkRepository = NetWorkRepository()

    val facltNmList = mutableListOf<String>()
    val addr1List = mutableListOf<String>()
    val mapXList = mutableListOf<String>()
    val mapYList = mutableListOf<String>()


    fun getCampsiteList() = viewModelScope.launch {
        val result = netWorkRepository.getCampList()
        val campsites = result.response.body.items.item

        for (campsite in campsites) {
            facltNmList.add(campsite.facltNm)
            addr1List.add(campsite.addr1)
            mapXList.add(campsite.mapX)
            mapYList.add(campsite.mapY)
        }

    }

}