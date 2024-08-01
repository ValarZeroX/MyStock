package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.banshus.mystock.data.entities.StockAccount

@Dao
interface StockAccountDao {
    @Query("SELECT * FROM stock_account")
    fun getStockAccount(): LiveData<StockAccount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stockAccount: StockAccount)
}