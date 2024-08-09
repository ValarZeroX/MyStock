package com.banshus.mystock.api.service

import com.banshus.mystock.api.response.StockDayAllTwResponse
import retrofit2.http.GET

interface TwseApiService {
    @GET("v1/exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayAll(): List<StockDayAllTwResponse>
}