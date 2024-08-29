package com.banshus.mystock.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.banshus.mystock.data.dao.StockSymbolDao
import com.banshus.mystock.repository.StockPriceApiRepository
import com.banshus.mystock.work.StockPriceUpdateWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class StockPriceViewModel(
    private val context: Context,
    private val stockPriceApiRepository: StockPriceApiRepository,
    private val stockSymbolDao: StockSymbolDao
) : ViewModel() {

    // 启动定时任务
    fun startStockPriceUpdate(intervalHours: Long) {
        viewModelScope.launch {
            val workRequest = PeriodicWorkRequestBuilder<StockPriceUpdateWorker>(
                intervalHours, TimeUnit.HOURS
            ).setInputData(workDataOf(
                "KEY_REPOSITORY" to stockPriceApiRepository, // 传递参数
                "KEY_DAO" to stockSymbolDao // 传递参数
            )).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "StockPriceUpdate",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }

    // 停止定时任务
    fun stopStockPriceUpdate() {
        viewModelScope.launch {
            WorkManager.getInstance(context).cancelUniqueWork("StockPriceUpdate")
        }
    }

    // 立即执行一次更新任务
    fun forceUpdateStockPrices() {
        viewModelScope.launch {
            val workRequest = OneTimeWorkRequestBuilder<StockPriceUpdateWorker>()
                .setInputData(workDataOf(
                    "KEY_REPOSITORY" to stockPriceApiRepository, // 传递参数
                    "KEY_DAO" to stockSymbolDao // 传递参数
                ))
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}

class StockPriceViewModelFactory(
    private val context: Context,
    private val repository: StockPriceApiRepository,
    private val stockSymbolDao: StockSymbolDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockPriceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockPriceViewModel(context, repository, stockSymbolDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}