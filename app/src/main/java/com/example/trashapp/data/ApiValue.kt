package com.example.trashapp.data

import android.provider.ContactsContract.CommonDataKinds.Nickname
import kotlinx.serialization.Serializable

@Serializable
data class ApiValue(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val roadviewImgpath: String,
    val nickname: String,
    val categories: String,
    val state: String,
    val date: String,
    val address: String? = "",
    val detailAddress: String? = ""
)