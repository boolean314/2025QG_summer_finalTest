package com.example.pmp.data.apiService

import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.EncryptLogin
import com.example.pmp.data.model.EncryptModify
import com.example.pmp.data.model.EncryptRegister
import com.example.pmp.data.model.ErrorStat
import com.example.pmp.data.model.ErrorTimes
import com.example.pmp.data.model.ResultResponse
import okhttp3.MultipartBody
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

   // http://192.168.1.233:8080/graph/getErrorTrend  三个参数project,module,type,获取错误量
    @GET("/graph/getErrorTrend")
fun getErrorTimes(@Query("projectId")projectId:String,@Query("startTime")startTime:String,@Query("endTime")endTime:String): Call<ApiResponse<List<ErrorTimes>>>

//获取移动错误排名
@GET("graph/getMobileErrorStats")
fun getMobileErrorStats(@Query("projectId")projectId: String):Call<ApiResponse<List<List<ErrorStat>>>>















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
}