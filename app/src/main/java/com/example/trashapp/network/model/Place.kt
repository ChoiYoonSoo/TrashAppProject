package com.example.trashapp.network.model

data class Place (
    var address_name: String, // 전체 지번 주소
    var road_address_name: String, // 전체 도로명 주소
    var x: String, // X 좌표값 혹은 longitude
    var y: String // Y 좌표값 혹은 latitude
)
