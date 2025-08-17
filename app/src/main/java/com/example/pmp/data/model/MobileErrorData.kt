package com.example.pmp.data.model

data class MobileErrorData(val projectId:String,val id:Int,val errorType:String,val message:String,val timestamp:String,val stack:String,val name:String,val delegatorId:Long,val responsibleId:Long,val avatarUrl:String) {

}