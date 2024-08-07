package com.banshus.mystock.api

import com.banshus.mystock.api.response.StockChartResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YahooFinanceApiService {
    @GET("v8/finance/chart/{symbol}")
    suspend fun getStockPrice(
        @Path("symbol") symbol: String,
        @Query("period1") period1: Long,
        @Query("period2") period2: Long,
        @Query("interval") interval: String = "1d",
        @Query("events") events: String = "history"
    ): StockChartResponse
}