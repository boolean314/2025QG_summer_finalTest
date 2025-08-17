package com.example.pmp.data.model

data class ListMissions(
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
    val responsibleId: Int?,
    var isHandled: Boolean,

)
