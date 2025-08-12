package com.example.pmp.data.model

data class UserInfo(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val avatar: String,
    val isDeleted: Boolean,
    val createdTime: String,
    val phone: String
)
