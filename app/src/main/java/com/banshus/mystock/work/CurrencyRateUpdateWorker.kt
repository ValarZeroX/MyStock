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
                    Log.d("CurrencyRateUpdateWorker", "Starting currency rate update")

                    val currencyApiRepository = CurrencyApiRepository(RetrofitInstance.currencyApi)
                    val database = AppDatabase.getDatabase(context)
                    val currencyDao = database.currencyDao()

                    // 获取最新的汇率数据
                    val response = currencyApiRepository.fetchCurrencyRates()

                    response?.forEach { (currencyCode, currencyRate) ->
                        val currency = Currency(
                            currencyCode = currencyCode,
                            exchangeRate = currencyRate.exchangeRate
                        )
                        currencyDao.insertCurrency(currency)
                    }

                    Log.d("CurrencyRateUpdateWorker", "Currency rates updated successfully")
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