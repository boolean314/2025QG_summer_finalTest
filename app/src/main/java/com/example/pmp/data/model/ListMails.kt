package com.example.pmp.data.model

data class ListMails(
    val errorId: Int?,
    val errorMessage: String,
    val errorType: String,
    val id: Int?,
    val platform: String,
    val projectId: String,
    val projectName: String,
    val responsibleName: String,
    val senderAvatar: String,
    val senderName: String,
    val receiverId: Int?,
    var isHandled: Boolean,
)
