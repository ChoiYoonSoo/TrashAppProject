package com.example.trashapp.network.model

data class User (
    val email : String,
    val nickname : String,
    val profileImgPath : String?,
    val roleType : String,
)