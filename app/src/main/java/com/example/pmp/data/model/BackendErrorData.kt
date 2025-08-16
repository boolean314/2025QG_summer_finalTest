package com.example.pmp.data.model

data class BackendErrorData (val id:Int,val errorType:String,val timestamp:String,val stack:String,val name:String,val delegatorId:Long,val responsibleId:Long,val avatarUrl:String){
}