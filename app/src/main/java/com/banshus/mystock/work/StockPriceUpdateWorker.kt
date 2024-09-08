package com.banshus.mystock.work

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.banshus.mystock.api.RetrofitInstance
import com.banshus.mystock.repository.StockPriceApiRepository
import com.banshus.mystock.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockPriceUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        suspend fun updateStockPrices(context: Context) {
            withContext(Dispatchers.IO) {
                try {
                    val stockPriceApiRepository = StockPriceApiRepository(RetrofitInstance.yahooApi)
                    val database = AppDatabase.getDatabase(context)
                    val stockSymbolDao = database.stockSymbolDao()
                    val stockMarketDao = database.stockMarketDao()
                    // 从数据库中获取所有股票符号
                    val stockSymbols = stockSymbolDao.fetchAllStockSymbols()

                    val stockMarkets = stockMarketDao.getAllStockMarketsWorker()
                    // 遍历每个股票符号，获取对应的股票市场代码，并通过 API 获取最新价格
                    val stockMarketMap = stockMarkets.associateBy { it.stockMarket }
                    // 遍历每个股票符号，获取最新价格并更新数据库
                    stockSymbols.forEach { stockSymbol ->
                        val stockMarketCode = stockMarketMap[stockSymbol.stockMarket]?.stockMarketCode
                        if (stockMarketCode != null) {
                            val response = stockPriceApiRepository.getStockPriceWorker(
                                symbol = stockSymbol.stockSymbol,
                                marketCode = stockMarketCode, // 传递股票市场代码
                                period1 = System.currentTimeMillis() / 1000 - 86400, // 一天前的时间戳
                                period2 = System.currentTimeMillis() / 1000 // 当前时间戳
                            )

                            response?.chart?.result?.firstOrNull()?.let { result ->
                                val updatedStockSymbol = stockSymbol.copy(
                                    stockPrice = result.meta.regularMarketPrice,
                                    regularMarketDayLow = result.meta.regularMarketDayLow,
                                    regularMarketDayHigh = result.meta.regularMarketDayHigh,
                                    chartPreviousClose = result.meta.chartPreviousClose,
                                    lastUpdatedTime = System.currentTimeMillis()
                                )

                                // 更新数据库中的股票符号信息
                                stockSymbolDao.insertStockSymbol(updatedStockSymbol)
                            }
                        } else {
                            Log.w(TAG, "No stockMarketCode found for stockMarket: ${stockSymbol.stockMarket}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating stock prices", e)
                }
            }
        }
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // 调用静态方法进行更新操作
                updateStockPrices(applicationContext)
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
}