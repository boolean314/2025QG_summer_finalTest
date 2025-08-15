package com.example.pmp.data.model

data class AverageTimeResponse(
    val data: Map<String, Double>,
    val code: Int,
    val msg: String
)

// 用于表示单个API平均时间的数据类（用于图表展示）
data class AverageTime(
    val api: String,
    val averageTime: Long
)