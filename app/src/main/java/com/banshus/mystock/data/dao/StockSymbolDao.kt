package com.banshus.mystock.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.banshus.mystock.data.entities.StockSymbol

@Dao
interface StockSymbolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockSymbol(stockSymbol: StockSymbol)

    @Query("SELECT * FROM stock_symbol WHERE stockMarket = :stockMarket ORDER BY stockSymbol ASC")
    suspend fun fetchStockSymbolsListByMarket(stockMarket: Int): List<StockSymbol>

//    @Query("SELECT COUNT(*) FROM stock_symbol WHERE stockSymbol = :stockSymbol AND stockMarket = :stockMarket")
//    suspend fun countBySymbolAndMarket(stockSymbol: String, stockMarket: Int): Int

//    @Query("UPDATE stock_symbol SET stockName = :stockName WHERE stockSymbol = :stockSymbol AND stockMarket = :stockMarket")
//    suspend fun updateStockSymbol(stockSymbol: String, stockMarket: Int, stockName: String)


}