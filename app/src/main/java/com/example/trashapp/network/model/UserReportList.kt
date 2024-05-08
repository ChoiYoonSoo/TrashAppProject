package com.example.trashapp.network.model

data class UserReportList(
    val id : Long,
    val userId : Long,
    val trashcanId : Long,
    val reportCategory : String,
    val modifyStatus : Boolean
)
