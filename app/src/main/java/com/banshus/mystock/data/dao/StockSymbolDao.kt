package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockSymbol

@Dao
interface StockSymbolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockSymbol(stockSymbol: StockSymbol)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockSymbols(stockSymbols: List<StockSymbol>)

    @Query("SELECT * FROM stock_symbol WHERE stockMarket = :stockMarket ORDER BY stockSymbol ASC")
    suspend fun fetchStockSymbolsListByMarket(stockMarket: Int): List<StockSymbol>

    @Query("UPDATE stock_symbol SET stockName = :newName WHERE stockSymbol = :symbol AND stockMarket = :marketId")
    suspend fun updateStockName(symbol: String, marketId: Int, newName: String)

    @Query("SELECT * FROM stock_symbol ORDER BY stockSymbol ASC")
    suspend fun fetchAllStockSymbols(): List<StockSymbol>

    @Query("SELECT * FROM stock_symbol ORDER BY stockSymbol ASC")
    suspend fun getAllStockSymbolsSync(): List<StockSymbol>
}