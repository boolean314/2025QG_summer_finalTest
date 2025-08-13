package com.example.pmp.data.retrofit

import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.util.LocalDataTimeDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

object RetrofitClient {
    val instance: MyApiService by lazy {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDataTimeDeserializer()) // 注册自定义解析器
            .create()

        Retrofit.Builder()
            .baseUrl("http://47.113.224.195:32406/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApiService::class.java)
    }
}