package com.example.trashapp.data

data class TmapApiRequest (
    val startX: Double,
    val startY: Double,
    val endX: Double,
    val endY: Double,
    val startName: String,
    val endName: String,
    val searchOptIn: Int
)