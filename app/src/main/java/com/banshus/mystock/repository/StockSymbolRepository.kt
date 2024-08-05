package com.banshus.mystock.repository

import com.banshus.mystock.data.dao.StockSymbolDao
import com.banshus.mystock.data.entities.StockSymbol

class StockSymbolRepository(private val stockSymbolDao: StockSymbolDao) {
    suspend fun fetchStockSymbolsListByMarket(stockMarket: Int): List<StockSymbol> {
        return stockSymbolDao.fetchStockSymbolsListByMarket(stockMarket)
    }

    suspend fun insertStockSymbol(stockSymbol: StockSymbol) {
        stockSymbolDao.insertStockSymbol(stockSymbol)
    }
}