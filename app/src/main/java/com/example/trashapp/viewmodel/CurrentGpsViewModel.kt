package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CurrentGpsViewModel : ViewModel() {
    private val _latitude = MutableLiveData<Double>()
    var latitude: LiveData<Double> = _latitude

    private val _longitude = MutableLiveData<Double>()
    var longitude: LiveData<Double> = _longitude

    fun setGps(latitude: Double, longitude: Double) = viewModelScope.launch {
        _latitude.postValue(latitude)
        _longitude.postValue(longitude)
    }
}