package com.example.pmp.data.retrofit

import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.util.LocalDataTimeDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object AIRetrofitClient {
    val instance: MyApiService by lazy {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDataTimeDeserializer()) // 注册自定义解析器
            .create()

        Retrofit.Builder()
            .baseUrl("http://192.168.1.100:8000/")    //lrt：http://47.113.224.195:32406
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApiService::class.java)
    }
}