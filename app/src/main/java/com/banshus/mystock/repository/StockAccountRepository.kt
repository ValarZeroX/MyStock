package com.banshus.mystock.repository

import com.banshus.mystock.data.dao.StockAccountDao
import com.banshus.mystock.data.entities.StockAccount

class StockAccountRepository(private val stockAccountDao: StockAccountDao) {
    suspend fun insertStockAccount(stockAccount: StockAccount) {
        stockAccountDao.insert(stockAccount)
    }
}