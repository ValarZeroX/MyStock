package com.banshus.mystock.repository

import android.util.Log
import com.banshus.mystock.api.YahooFinanceApiService
import com.banshus.mystock.api.response.StockChartResponse

class StockPriceApiRepository(private val apiService: YahooFinanceApiService) {
    suspend fun getStockPrice(symbol: String, period1: Long, period2: Long): StockChartResponse {
        return try {
            apiService.getStockPrice(symbol, period1, period2)
        } catch (e: Exception) {
            // Log or handle the error
            Log.e("StockPriceApiRepository", "Error fetching stock price", e)
            throw e
        }
    }
}