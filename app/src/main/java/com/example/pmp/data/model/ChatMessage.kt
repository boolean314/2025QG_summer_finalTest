package com.example.pmp.data.model

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val isLoading: Boolean = false
)
