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
    fun getAllStockAccounts(): LiveData<List<StockAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stockAccount: StockAccount)

    @Query("SELECT * FROM stock_account LIMIT 1")
    fun getFirstStockAccount(): LiveData<StockAccount?>

    @Query("SELECT * FROM stock_account WHERE accountId = :accountId")
    fun getStockAccountByID(accountId: Int): LiveData<StockAccount?>
}