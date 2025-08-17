// BackendErrorData.kt
package com.example.pmp.data.model

data class BackendErrorData(
    val projectId: String,
    override val id: Int,
    val environment: String,
    override val errorType: String,
    override val timestamp: String,
    val stack: String,
    override val name: String?,
    val delegatorId: Long,
    val responsibleId: Long,
    override val avatarUrl: String
) : BaseErrorData {

}
