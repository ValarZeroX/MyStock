package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockMarket

@Dao
interface StockAccountDao {
    @Query("SELECT * FROM stock_account ORDER BY accountSort")
    fun getAllStockAccounts(): LiveData<List<StockAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stockAccount: StockAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockAccounts(stockAccounts: List<StockAccount>)

    @Query("SELECT * FROM stock_account LIMIT 1")
    fun getFirstStockAccount(): LiveData<StockAccount?>

    @Query("SELECT * FROM stock_account WHERE accountId = :accountId")
    fun getStockAccountByID(accountId: Int): LiveData<StockAccount?>

    @Update
    suspend fun updateAll(stockAccounts: List<StockAccount>)

    @Query("DELETE FROM stock_account WHERE accountId = :accountId")
    suspend fun deleteStockAccountById(accountId: Int)

    @Update
    suspend fun updateStockAccount(stockAccount: StockAccount)

    @Query("SELECT * FROM stock_account ORDER BY accountSort")
    suspend fun getAllStockAccountsSync(): List<StockAccount>
}