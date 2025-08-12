package com.example.pmp.data.apiService

import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.ErrorStat
import com.example.pmp.data.model.ErrorTimes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyApiService {

   // http://192.168.1.233:8080/graph/getErrorTrend  三个参数project,module,type,获取错误量
    @GET("/graph/getErrorTrend")
fun getErrorTimes(@Query("projectId")projectId:String,@Query("startTime")startTime:String,@Query("endTime")endTime:String): Call<ApiResponse<List<ErrorTimes>>>

//获取移动错误排名
@GET("graph/getMobileErrorStats")
fun getMobileErrorStats(@Query("projectId")projectId: String):Call<ApiResponse<List<List<ErrorStat>>>>

}

