package com.example.pmp.data.model

import java.time.LocalDateTime

data class ProjectDetail(
    val uuid: String,
    val name: String,
    val description: String,
    val createdTime: String,
    val isPublic: Boolean,
    val webhook: String,
    val inviteCode: String,
    val groupCode: String,
    val isDeleted: Boolean
)
