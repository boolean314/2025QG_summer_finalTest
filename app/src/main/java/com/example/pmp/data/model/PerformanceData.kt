package com.example.pmp.data.model

// 性能数据类
data class PerformanceData(
    val timestamp: String,
    val operationId: String?,
    val operationFps: Int?,
    val memoryUsage: MemoryUsage?
)