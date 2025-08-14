package com.example.pmp.data.apiService

import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.AverageTimeResponse
import com.example.pmp.data.model.CreateProjectData
import com.example.pmp.data.model.EncryptLogin
import com.example.pmp.data.model.EncryptModify
import com.example.pmp.data.model.EncryptRegister
import com.example.pmp.data.model.ErrorStat
import com.example.pmp.data.model.ErrorTimes
import com.example.pmp.data.model.ManualTrackingStats
import com.example.pmp.data.model.ResultResponse
import okhttp3.MultipartBody
import okhttp3.internal.platform.Platform
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.File


interface MyApiService {

    //创建项目时发送项目信息
    @POST("projects")
    fun createProject(@Body createProjectData: CreateProjectData): Call<ApiResponse<Any>>


    //获取三端错误量
    @GET("graph/getErrorTrend")
fun getErrorTimes(@Query("projectId")projectId:String,@Query("startTime")startTime:String,@Query("endTime")endTime:String): Call<ApiResponse<List<ErrorTimes>>>

//获取近一周移动错误排名
@GET("graph/getMobileErrorStatsPro")
fun getMobileErrorStatsPro(@Query("projectId")projectId: String):Call<ApiResponse<List<List<ErrorStat>>>>

    //获取近一周前端错误排名
    @GET("graph/getFrontendErrorStats")
    fun getFrontendErrorStats(@Query("projectId")projectId: String):Call<ApiResponse<List<List<ErrorStat>>>>

    //获取近一周后端错误排名
    @GET("graph/getBackendErrorStatsPro")
    fun getBackendErrorStatsPro(@Query("projectId")projectId: String):Call<ApiResponse<List<List<ErrorStat>>>>

//获取三端请求用时
    @GET("graph/getAverageTime")
 fun getAverageTime(@Query("projectId")projectId: String,@Query("platform")platform:String,@Query("timeType")timeType:String):Call<AverageTimeResponse>

 //展示前端用户埋点标签及点击次数
 @GET("graph/getManualTrackingStats")
    fun getManualTrackingStats(@Query("projectId")projectId: String,@Query("startTime")startTime: String,@Query("endTime")endTime: String):Call<ApiResponse<List<ManualTrackingStats>>>




    @POST("users/password")
    suspend fun login(@Body userEncrypted: EncryptLogin): ResultResponse

    @POST("users/register")
    suspend fun register(@Body userRegister: EncryptRegister): ResultResponse

    @GET("users/sendCodeByEmail")
    suspend fun getVerifyCode(
        @Query("encryptedData") encryptedData: String,
        @Query("encryptedKey") encryptedKey: String
    ): ResultResponse

    @PUT("users/updateUser")
    suspend fun modifyU(@Body userModify: EncryptModify): ResultResponse

    @PUT("users/findPassword")
    suspend fun modifyP(@Body userModify: EncryptModify): ResultResponse

    @Multipart
    @POST("users/updateAvatar")
    suspend fun modifyAvatar(
        @Query("userId") userId: Int,
        @Part avatar: MultipartBody.Part
    ): ResultResponse

    @GET("projects/getPersonalProject")
    suspend fun getPersonalProject(@Query("userId") userId: Long): ResultResponse

    @GET("projects/getPublicProjectList")
    suspend fun getPublicProject() : ResultResponse

    @DELETE("projects")
    suspend fun deleteProject(@Query("uuid") uuid: String): ResultResponse

    @GET("roles/getRole")
    suspend fun authentication(
        @Query("userId") userId: Int,
        @Query("projectId") projectId: String
    ): ResultResponse
}