package com.example.pmp.data.model

data class ThresholdData(
    val errorType: String,
    val env: String,
    val projectId: String,
    val platform: String,
    val threshold: Int
)
