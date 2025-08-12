package com.example.pmp.data.model

 data class ApiResponse<T>(
    val code: Int,
    val msg: String,
    val data: T
)
