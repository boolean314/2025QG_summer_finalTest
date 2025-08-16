package com.example.pmp.data.model

import java.sql.Timestamp
import java.sql.Types

data class FrontErrorData(val id:Int,val projectId:String,val timestamp: String,val errorType:String,val message:String,val stack:String,val environment:String,val colno:Int,val lineno:Int,val jsFilename:String,val name:String,val delegatorId:Long,val responsibleId:Long,val avatarUrl:String) {
}
