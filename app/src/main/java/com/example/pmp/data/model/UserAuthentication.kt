package com.example.pmp.data.model

data class UserAuthentication(
    val userId: Long,
    val projectId: Long,
    val power: Int,
    val userRole: Int
)
