package com.example.pmp.data.model

data class BackendErrorData (val projectId:String,val id:Int,val environment:String,val errorType:String,val timestamp:String,val stack:String,val name:String,val delegatorId:Long,val responsibleId:Long,val avatarUrl:String){
}