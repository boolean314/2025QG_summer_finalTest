package com.example.pmp.data.model

import java.net.InetAddress

data class MemberListData(
    val id: Long,
    val userId: Long,
    val username: String,
    val avatar: String,
    val userRole:Int,
    val power:Int
) {
}