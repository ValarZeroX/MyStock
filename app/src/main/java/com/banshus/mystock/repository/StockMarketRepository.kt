package com.banshus.mystock.repository

import androidx.lifecycle.LiveData
import com.banshus.mystock.data.dao.StockMarketDao
import com.banshus.mystock.data.entities.StockMarket

class StockMarketRepository(private val stockMarketDao: StockMarketDao) {
    val allStockMarkets: LiveData<List<StockMarket>> = stockMarketDao.getAllStockMarkets()

    suspend fun insert(stockMarket: StockMarket) {
        stockMarketDao.insertStockMarket(stockMarket)
    }

    suspend fun update(stockMarket: StockMarket) {
        stockMarketDao.updateStockMarket(stockMarket)
    }

    suspend fun delete(stockMarket: StockMarket) {
        stockMarketDao.deleteStockMarket(stockMarket)
    }

    suspend fun updateAll(stockMarkets: List<StockMarket>) {
        stockMarketDao.updateAll(stockMarkets)
    }

    suspend fun getStockMarketById(stockMarket: Int): StockMarket? {
        return stockMarketDao.getStockMarketById(stockMarket)
    }
}