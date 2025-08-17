package com.example.pmp.data.model

import kotlin.time.Duration

data class FrontendErrorData(
    val id: Int,
    val projectId: String,
    val timestamp: String,
    val errorType: String,
    val message: String,
    val stack: String,
    val duration: Long,
    val colno: Int? = null,
    val lineno: Int? = null,
    val jsFilename: String? = null,
    val name: String? = null,
    val delegatorId: Long? = null,
    val responsibleId: Long? = null,
    val avatarUrl: String? = null,
    val tag:tagData,
    val request:requestData,
    val response:responseData,
    val userAgent:String,
)
data class tagData(val environment:String?)
data class requestData(val url:String?,val method:String?)
data class responseData(val status:Int?,val statusText:String?)

