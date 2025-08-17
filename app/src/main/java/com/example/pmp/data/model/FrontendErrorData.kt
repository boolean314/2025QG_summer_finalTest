// FrontendErrorData.kt
package com.example.pmp.data.model

import kotlin.time.Duration

data class FrontendErrorData(
    override val id: Int,
    val projectId: String,
    override val timestamp: String,
    override val errorType: String,
    val message: String,
    val stack: String,
    val duration: Long,
    val colno: Int? = null,
    val lineno: Int? = null,
    val jsFilename: String? = null,
    override val name: String? = null,
    val delegatorId: Long? = null,
    val responsibleId: Long? = null,
    override val avatarUrl: String? = null,
    val tag: tagData,
    val request: requestData,
    val response: responseData,
    val userAgent: String,
) : BaseErrorData

data class tagData(val environment: String?)
data class requestData(val url: String?, val method: String?)
data class responseData(val status: Int?, val statusText: String?)
