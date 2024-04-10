package com.example.trashapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebViewViewModel : ViewModel(){

//    private val _latitude = MutableLiveData<Double>()
//    var latitude : LiveData<Double> = _latitude
//
//    private val _longitude = MutableLiveData<Double>()
//    var longitude : LiveData<Double> = _longitude

    var latitude : Double = 0.0
    var longitude : Double = 0.0
}