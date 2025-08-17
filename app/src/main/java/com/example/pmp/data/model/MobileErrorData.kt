// MobileErrorData.kt
package com.example.pmp.data.model

data class MobileErrorData(
    val projectId: String,
    override val id: Int,
    override val errorType: String,
    val message: String,
    override val timestamp: String,
    val stack: String,
    override val name: String?,
    val delegatorId: Long,
    val responsibleId: Long,
    override val avatarUrl: String
) : BaseErrorData {

}
