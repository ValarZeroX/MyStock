package com.banshus.mystock.repository

import android.util.Log
import com.banshus.mystock.api.response.StockDayAllTwResponse
import com.banshus.mystock.api.service.TwseApiService

class TwseApiRepository(private val apiService: TwseApiService) {
    suspend fun getStockDayAll(): List<StockDayAllTwResponse> {
        return try {
            apiService.getStockDayAll()
        } catch (e: Exception) {
            // Log or handle the error
            Log.e("TwseApiRepository", "Error fetching stock day data", e)
            throw e
        }
    }
}
