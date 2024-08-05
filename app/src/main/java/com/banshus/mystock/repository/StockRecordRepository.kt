package com.banshus.mystock.repository

import com.banshus.mystock.data.dao.StockRecordDao
import com.banshus.mystock.data.entities.StockRecord

class StockRecordRepository(private val stockRecordDao: StockRecordDao) {
    suspend fun insertStockRecord(stockRecord: StockRecord) {
        stockRecordDao.insertStockRecord(stockRecord)
    }
}