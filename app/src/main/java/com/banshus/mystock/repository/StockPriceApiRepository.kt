package com.banshus.mystock.repository

import android.util.Log
import com.banshus.mystock.api.service.YahooFinanceApiService
import com.banshus.mystock.api.response.StockChartResponse
import com.banshus.mystock.api.response.StockSearchResponse
import retrofit2.HttpException

class StockPriceApiRepository(private val apiService: YahooFinanceApiService) {
    suspend fun getStockPrice(symbol: String, period1: Long, period2: Long): StockChartResponse? {
        return try {
            apiService.getStockPrice(symbol, period1, period2)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
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

    suspend fun getStockPriceWorker(symbol: String, period1: Long, period2: Long, marketCode: String): StockChartResponse? {
        return try {
            val combinedSymbol: String = if (marketCode != "US") {
                "${symbol}.${marketCode}"
            } else {
                symbol
            }
            apiService.getStockPrice(combinedSymbol, period1, period2)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
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

    suspend fun searchStock(symbol: String): StockSearchResponse? {
        return try {
            apiService.searchStock(symbol)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
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