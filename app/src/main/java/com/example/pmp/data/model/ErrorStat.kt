package com.example.pmp.data.model

data class ErrorStat(
    val errorType: String,
    val count: Int? = null,
    val ratio: Double? = null
)