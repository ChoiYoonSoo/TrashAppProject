package com.example.trashapp.network.model

data class CancelReport(
    val trashcanId: String,
    val reportCategory: String,
    val reasonReportCancel: String
)
