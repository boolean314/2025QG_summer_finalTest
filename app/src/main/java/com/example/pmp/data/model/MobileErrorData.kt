package com.example.pmp.data.model

data class MobileErrorData(val id:Long,val errorType:String,val message:String,val timestamp:String,val stack:String,val name:String,val delegatorId:Long,val responsibleId:Long,val avatarUrl:String) {

}