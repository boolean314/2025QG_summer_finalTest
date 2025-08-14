package com.example.pmp.data.model

import java.time.LocalDateTime

data class PersonalProject(
    val uuid: String,
    val name: String,
    val description: String,
    val createTime: String,
    val isPublic: Boolean,
    val id: Long,
    val userId: Long,
    val power: Int?,
    val userRole: Int?
)
