package com.example.pmp.data.apiService

import com.example.pmp.data.model.ResultResponse
import com.example.pmp.data.model.EncryptLogin
import com.example.pmp.data.model.EncryptRegister
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyApiService {

    @POST("users/password")
    suspend fun login(@Body userEncrypted: EncryptLogin): ResultResponse

    @POST("users/register")
    suspend fun register(@Body userRegister: EncryptRegister): ResultResponse

    @GET("users/sendCodeByEmail")
    suspend fun getVerifyCode(
        @Query("encryptedData") encryptedData: String,
        @Query("encryptedKey") encryptedKey: String
    ): ResultResponse

}