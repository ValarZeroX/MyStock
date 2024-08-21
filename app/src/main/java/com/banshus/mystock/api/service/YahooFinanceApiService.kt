package com.banshus.mystock.api.service

import com.banshus.mystock.api.response.StockChartResponse
import com.banshus.mystock.api.response.StockSearchResponse
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
        @Query("events") events: String = ""
    ): StockChartResponse

    @GET("v1/finance/search")
    suspend fun searchStock(
        @Query("q") query: String
    ): StockSearchResponse
}