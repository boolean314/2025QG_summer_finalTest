package com.example.pmp.data.model

interface BaseErrorData {
    val id: Int
    val errorType: String
    val timestamp: String
    val name: String?
    val avatarUrl: String?
}