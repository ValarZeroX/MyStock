package com.banshus.mystock.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://query1.finance.yahoo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: YahooFinanceApiService by lazy {
        retrofit.create(YahooFinanceApiService::class.java)
    }
}