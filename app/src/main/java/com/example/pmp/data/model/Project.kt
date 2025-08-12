package com.example.pmp.data.model

data class Project(
    val projectId: String,
    val name: String,
    val platform: String,
    val status: String,
    val description: String,
    val createdDate: String,
    val shareCode: String,
    val botUrl: String
)
