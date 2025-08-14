package com.example.pmp.data.model

data class CreateProjectData(
    val name: String,
    val description: String,
    val isPublic: Boolean,
    val userId: Int,
)
