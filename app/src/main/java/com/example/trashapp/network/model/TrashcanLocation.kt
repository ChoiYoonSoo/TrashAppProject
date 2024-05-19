package com.example.trashapp.network.model

import com.example.trashapp.data.Coordinate

data class TrashcanLocation(
    val coordinates: List<Coordinate>,
    val categories: String,
    val detailAddress: String,
)
