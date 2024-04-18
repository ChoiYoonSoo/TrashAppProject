package com.example.trashapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashapp.data.Location
import kotlinx.coroutines.launch

class CurrentGpsViewModel : ViewModel() {

    private val _lastLocation = MutableLiveData<Location>()

    var lastLocation: LiveData<Location> = _lastLocation

    private val _currentLocation = MutableLiveData<Location>()

    var currentLocation: LiveData<Location> = _currentLocation

    var location : Location? = null

    var isGpsEnabled = MutableLiveData<Boolean>()

    fun setGps(latitude: Double, longitude: Double) = viewModelScope.launch {
        if(location == null || location!!.latitude != latitude || location!!.longitude != longitude){
            location = Location(latitude, longitude)
            _lastLocation.postValue(location!!)
        }
    }

    fun currentGps(latitude: Double, longitude: Double) = viewModelScope.launch {
        location = Location(latitude, longitude)
        _currentLocation.postValue(location)
    }

    fun setGpsEnabled(isEnabled: Boolean) = viewModelScope.launch {
        isGpsEnabled.postValue(isEnabled)
    }
}