package com.example.trashapp.data

import com.google.gson.annotations.SerializedName

data class ApiValue (
    @SerializedName("경도") val longitude: String,
    @SerializedName("신호등종류") val type: String,
    @SerializedName("연번") val serialNumber: Int,
    @SerializedName("위도") val latitude: String,
    @SerializedName("자치구") val district: String,
    @SerializedName("주소") val address: String
)