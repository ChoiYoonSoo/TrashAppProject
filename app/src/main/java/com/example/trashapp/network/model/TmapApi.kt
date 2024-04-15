package com.example.trashapp.network.model

data class TmapApi (
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val totalDistance: Int,
    val totalTime: Int
)