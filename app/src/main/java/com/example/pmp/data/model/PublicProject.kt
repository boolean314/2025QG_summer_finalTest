package com.example.pmp.data.model

import java.time.LocalDateTime

data class PublicProject(
    val uuid: String,
    val name: String,
    val description: String,
    val createdTime: String,
    val isPublic: Boolean,
    val webhook: String,
    val invitedCode: String,
    val groupCode: String,
    val isDeleted: Boolean
)
