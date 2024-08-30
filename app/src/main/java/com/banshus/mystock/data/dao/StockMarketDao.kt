package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banshus.mystock.data.entities.StockMarket

@Dao
interface StockMarketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMarket(stockMarket: StockMarket)

    @Update
    suspend fun updateStockMarket(stockMarket: StockMarket)

    @Delete
    suspend fun deleteStockMarket(stockMarket: StockMarket)

    @Query("SELECT * FROM stock_market WHERE stockMarket = :stockMarket")
    suspend fun getStockMarketById(stockMarket: Int): StockMarket?

    @Update
    suspend fun updateAll(stockMarkets: List<StockMarket>)

    @Query("SELECT * FROM stock_market ORDER BY stockMarketSort")
    fun getAllStockMarkets(): LiveData<List<StockMarket>>

    @Query("SELECT * FROM stock_market ORDER BY stockMarketSort")
    suspend fun getAllStockMarketsWorker(): List<StockMarket>
}