package com.example.trashapp.network.model

data class UnknownTrashcan(
    val latitude: Double,
    val longitude: Double,
    val unknown_trashcan_id: Long,
    val userId : Long,
    val categories: String,
    val roadviewImgpath: String,
    val state: String,
    val date: String,
    val detailAddress : String
)
