package com.example.trashapp.network.model

data class ModifyTrashcan(
    val reportId : String,
    val trashcanId : String,
    val latitude : Double,
    val longitude : Double,
    val reportCategory: String
)
