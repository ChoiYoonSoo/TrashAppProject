package com.example.trashapp.data

import kotlinx.serialization.Serializable

@Serializable
data class ApiValue(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val roadviewImgpath: String,
    val userId: Int,
    val categories: String,
    val state: String,
    val address: String,
    val detailAddress: String
)