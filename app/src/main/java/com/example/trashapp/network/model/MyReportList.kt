package com.example.trashapp.network.model

data class MyReportList(
    val id: Long,
    val email: String,
    val trashcanId: Long,
    val reportCategory: String,
    val modifyStatus: Int,
    val detailAddress: String,
    val reasonReportCancel: String
)
