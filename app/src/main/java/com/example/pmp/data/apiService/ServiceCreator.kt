package com.example.pmp.data.apiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private  const val BASE_URL="http://192.168.1.233:8080"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun<T>create(myApiService:Class<T>):T=retrofit.create(myApiService)

}