package com.banshus.mystock.repository

import com.banshus.mystock.api.service.YahooFinanceApiService
import com.banshus.mystock.api.response.StockChartResponse
import retrofit2.HttpException

class StockPriceApiRepository(private val apiService: YahooFinanceApiService) {
    suspend fun getStockPrice(symbol: String, period1: Long, period2: Long): StockChartResponse? {
        return try {
            apiService.getStockPrice(symbol, period1, period2)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                throw e
            } else {
                // 处理其他HTTP错误
                throw e
            }
        } catch (e: Exception) {
            throw e
        }
    }
}