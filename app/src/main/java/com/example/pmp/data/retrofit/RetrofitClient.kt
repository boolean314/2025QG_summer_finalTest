package com.example.pmp.data.retrofit

import com.example.pmp.data.apiService.MyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: MyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://47.113.224.195:32406/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApiService::class.java)
    }
}