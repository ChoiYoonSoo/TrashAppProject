package com.example.trashapp.data

data class MapData(
    var name: String? = null,
    var addr: String? = null,
    var latitude : Double,  // 위도
    var longitude : Double, // 경도
    var imageUrl : String? = null
)
