package com.banshus.mystock.api

import com.banshus.mystock.api.service.TwseApiService
import com.banshus.mystock.api.service.YahooFinanceApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://query1.finance.yahoo.com/") // 原来的 base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val yahooApi: YahooFinanceApiService by lazy {
        retrofit.create(YahooFinanceApiService::class.java)
    }

    private val twseRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://openapi.twse.com.tw/") // TWSE API 的 base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val twseApi: TwseApiService by lazy {
        twseRetrofit.create(TwseApiService::class.java)
    }
}