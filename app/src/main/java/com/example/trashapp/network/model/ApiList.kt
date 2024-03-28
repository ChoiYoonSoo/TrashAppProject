package com.example.trashapp.network.model

import com.example.trashapp.data.ApiValue

data class ApiList (
    val currentCount: Int,
    val data: List<ApiValue>,
    val matchCount: Int,
    val page: Int,
    val perPage: Int,
    val totalCount: Int
)