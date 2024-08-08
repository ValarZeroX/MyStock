package com.banshus.mystock.repository

import androidx.lifecycle.LiveData
import com.banshus.mystock.data.dao.StockRecordDao
import com.banshus.mystock.data.entities.StockRecord

class StockRecordRepository(private val stockRecordDao: StockRecordDao) {
    suspend fun insertStockRecord(stockRecord: StockRecord) {
        stockRecordDao.insertStockRecord(stockRecord)
    }

    fun getStockRecordsByDateRangeAndAccount(accountId: Int, startDate: Long, endDate: Long): LiveData<List<StockRecord>> {
        return stockRecordDao.getStockRecordsByDateRangeAndAccount(accountId, startDate, endDate)
    }
}