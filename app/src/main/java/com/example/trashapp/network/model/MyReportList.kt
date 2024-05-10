package com.example.trashapp.network.model

data class MyReportList(
    val id: Long,
    val userId: Long,
    val trashcanId: Long,
    val reportCategory: String,
    val modifyStatus: Boolean
)
