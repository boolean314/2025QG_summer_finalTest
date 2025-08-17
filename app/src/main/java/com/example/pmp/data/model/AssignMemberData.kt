package com.example.pmp.data.model

data class AssignMemberData(
    val delegatorId:Long,
    val errorId: Int,
    val platform:String,
    val projectId:String,
    val responsibleId:Long
)
