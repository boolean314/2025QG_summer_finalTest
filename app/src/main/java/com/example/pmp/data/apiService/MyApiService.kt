package com.example.pmp.data.apiService

import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.AverageTimeResponse
import com.example.pmp.data.model.CreateProjectData
import com.example.pmp.data.model.EncryptLogin
import com.example.pmp.data.model.EncryptModify
import com.example.pmp.data.model.EncryptRegister
import com.example.pmp.data.model.ErrorStat
import com.example.pmp.data.model.ErrorTimes
import com.example.pmp.data.model.FrontErrorData
import com.example.pmp.data.model.IpInterceptionCount
import com.example.pmp.data.model.JoinProjectData
import com.example.pmp.data.model.ManualTrackingStats
import com.example.pmp.data.model.MemberListData
import com.example.pmp.data.model.ProjectDetail
import com.example.pmp.data.model.ResultResponse
import com.example.pmp.data.model.chatItem
import com.example.pmp.data.model.updateRoles
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query


interface MyApiService {

    //创建项目时发送项目信息
    @POST("projects")
    fun createProject(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Body createProjectData: CreateProjectData
    ): Call<ApiResponse<Any>>

    //加入项目
    @POST("projects/joinProject")
    fun joinProject(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Body joinProjectData: JoinProjectData
    ): Call<ApiResponse<Any>>


    //获取项目详情
    @GET("projects/getProject")
    fun getProjectDetail(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("uuid") uuid: String
    ): Call<ApiResponse<ProjectDetail>>

    //获取项目邀请码
    @GET("projects/getInviteCode")
    fun getInviteCode(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") uuid: String
    ): Call<ApiResponse<String>>

    //更新项目信息
    @PUT("projects/update")
    fun updateProject(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Body projectDetail: ProjectDetail
    ): Call<ApiResponse<Any>>

    //获取成员列表
    @GET("roles/getMemberList")
    fun getMemberList(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String
    ): Call<ApiResponse<List<MemberListData>>>

    //修改权限userRole
    @PUT("roles")
    fun updateRoles(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Body updateRoles: updateRoles
    ): Call<ApiResponse<Any>>

    //移除成员
    @DELETE("roles")
    fun deleteMember(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("userId") userId: Long
    ): Call<ApiResponse<Any>>


    //获取三端错误量
    @GET("graph/getErrorTrend")
    fun getErrorTimes(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): Call<ApiResponse<List<ErrorTimes>>>

    //获取近一周移动错误排名
    @GET("graph/getMobileErrorStatsPro")
    fun getMobileErrorStatsPro(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String
    ): Call<ApiResponse<List<List<ErrorStat>>>>

    //获取近一周前端错误排名
    @GET("graph/getFrontendErrorStats")
    fun getFrontendErrorStats(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String
    ): Call<ApiResponse<List<List<ErrorStat>>>>

    //获取近一周后端错误排名
    @GET("graph/getBackendErrorStatsPro")
    fun getBackendErrorStatsPro(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String
    ): Call<ApiResponse<List<List<ErrorStat>>>>

    //获取三端请求用时
    @GET("graph/getAverageTime")
    fun getAverageTime(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("platform") platform: String,
        @Query("timeType") timeType: String
    ): Call<AverageTimeResponse>

    //展示前端用户埋点标签及点击次数
    @GET("graph/getManualTrackingStats")
    fun getManualTrackingStats(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): Call<ApiResponse<List<ManualTrackingStats>>>

    //获取后端非法攻击统计
    @GET("graph/getIpInterceptionCount")
    fun getIpInterceptionCount(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): Call<ApiResponse<List<IpInterceptionCount>>>

    // 获取错误列表
    @GET("errors/selectByCondition")
    fun <T> getErrorList(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("platform") platform: String
    ): Call<ApiResponse<List<T>>>

    // 获取前端错误列表
    @GET("errors/selectByCondition")
    fun getFrontendErrorList(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("platform") platform: String
    ): Call<ApiResponse<List<FrontErrorData>>>

    // 获取后端错误列表
    @GET("errors/selectByCondition")
    fun getBackendErrorList(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("platform") platform: String
    ): Call<ApiResponse<List<FrontErrorData>>>

    // 获取移动端错误列表
    @GET("errors/selectByCondition")
    fun getMobileErrorList(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("platform") platform: String
    ): Call<ApiResponse<List<FrontErrorData>>>


    @POST("users/password")
    suspend fun login(
        @Body userEncrypted: EncryptLogin
    ): ResultResponse

    @POST("users/register")
    suspend fun register(
        @Body userRegister: EncryptRegister
    ): ResultResponse

    @GET("users/sendCodeByEmail")
    suspend fun getVerifyCode(
        @Query("encryptedData") encryptedData: String,
        @Query("encryptedKey") encryptedKey: String
    ): ResultResponse

    @PUT("users/updateUser")
    suspend fun modifyU(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Body userModify: EncryptModify
    ): ResultResponse

    @PUT("users/findPassword")
    suspend fun modifyP(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Body userModify: EncryptModify
    ): ResultResponse

    @PUT("users/findPassword")
    suspend fun findP(
        @Body userModify: EncryptModify
    ): ResultResponse

    @Multipart
    @POST("users/updateAvatar")
    suspend fun modifyAvatar(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("userId") userId: Long,
        @Part avatar: MultipartBody.Part
    ): ResultResponse

    @GET("projects/getPersonalProject")
    suspend fun getPersonalProject(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("userId") userId: Long
    ): ResultResponse

    @GET("projects/getPublicProjectList")
    suspend fun getPublicProject(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
    ): ResultResponse

    @DELETE("projects")
    suspend fun deleteProject(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("uuid") uuid: String
    ): ResultResponse

    @GET("roles/getRole")
    suspend fun authentication(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("userId") userId: Long,
        @Query("projectId") projectId: String
    ): ResultResponse

    @GET("roles/getBossCountByProjectId")
    suspend fun authenticationBossCount(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String
    ): ResultResponse

    @DELETE("roles")
    suspend fun exitProject(
        @Header("Authorization") token: String?,
        @Header("Rsakey") RsaKey: String?,
        @Query("projectId") projectId: String,
        @Query("userId") userId: Long
    ): ResultResponse

    @POST("messages/chat")
    suspend fun chat(
        @Body chatItem: chatItem
    ): ResultResponse
}