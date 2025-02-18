package com.banshus.mystock.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.banshus.mystock.api.RetrofitInstance
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.Currency
import com.banshus.mystock.repository.CurrencyApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRateUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        suspend fun updateCurrencyRates(context: Context) {
            withContext(Dispatchers.IO) {
                try {
                    val currencyApiRepository = CurrencyApiRepository(RetrofitInstance.currencyApi)
                    val database = AppDatabase.getDatabase(context)
                    val currencyDao = database.currencyDao()

                    // 获取最新的汇率数据
                    val response = currencyApiRepository.fetchCurrencyRates()
                    val stockAccounts = database.stockAccountDao().getAllStockAccountsSync()

                    val currentTime = System.currentTimeMillis()
                    response?.forEach { (currencyCode, currencyRate) ->
                        val accountsUsingCurrency = stockAccounts.filter { it.currency == currencyCode }
                        if ("USD$accountsUsingCurrency" == currencyCode || "$accountsUsingCurrency" == "USD"){
                            val currency = Currency(
                                currencyCode = "$accountsUsingCurrency",
                                exchangeRate = currencyRate.exchangeRate,
                                lastUpdatedTime = currentTime,
                            )
                            currencyDao.insertCurrency(currency)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CurrencyRateUpdateWorker", "Error updating currency rates", e)
                }
            }
        }
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                updateCurrencyRates(applicationContext)
                Result.success()
            } catch (e: Exception) {
                Log.e("CurrencyRateUpdateWorker", "Error during periodic currency rate update", e)
                Result.retry()
            }
        }
    }
}