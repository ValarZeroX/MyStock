package com.banshus.mystock.repository

import com.banshus.mystock.data.dao.StockSymbolDao
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockSymbol

class StockSymbolRepository(private val stockSymbolDao: StockSymbolDao) {
    suspend fun fetchStockSymbolsListByMarket(stockMarket: Int): List<StockSymbol> {
        return stockSymbolDao.fetchStockSymbolsListByMarket(stockMarket)
    }

    suspend fun fetchAllStockSymbols(): List<StockSymbol> {
        return stockSymbolDao.fetchAllStockSymbols() // 调用新方法
    }

    suspend fun insertStockSymbol(stockSymbol: StockSymbol) {
        stockSymbolDao.insertStockSymbol(stockSymbol)
    }

    suspend fun updateStockName(symbol: String, marketId: Int, newName: String) {
        stockSymbolDao.updateStockName(symbol, marketId, newName)
    }
}