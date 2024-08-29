package com.banshus.mystock.work

import android.content.Context
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

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // 从InputData中获取传递的参数
                val stockPriceApiRepository = StockPriceApiRepository(RetrofitInstance.yahooApi)
                val database = AppDatabase.getDatabase(applicationContext)
                val stockSymbolDao = database.stockSymbolDao()

                // 从数据库中获取所有股票符号
                val stockSymbols = stockSymbolDao.fetchAllStockSymbols()

                // 遍历每个股票符号，获取最新价格并更新数据库
                stockSymbols.forEach { stockSymbol ->
                    val response = stockPriceApiRepository.getStockPrice(
                        symbol = stockSymbol.stockSymbol,
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
                }
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
}