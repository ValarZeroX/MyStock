package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banshus.mystock.data.entities.StockRecord

@Dao
interface StockRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockRecord(stockRecord: StockRecord)

    @Update
    suspend fun updateStockRecord(stockRecord: StockRecord)

    @Query("DELETE FROM stock_record WHERE recordId = :recordId")
    suspend fun deleteStockRecordById(recordId: Int)

    @Query("SELECT * FROM stock_record WHERE accountId = :accountId AND transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate")
    fun getStockRecordsByDateRangeAndAccount(
        accountId: Int,
        startDate: Long,
        endDate: Long
    ): LiveData<List<StockRecord>>

    @Query("SELECT * FROM stock_record WHERE transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate")
    fun getStockRecordsByDateRange(
        startDate: Long,
        endDate: Long
    ): LiveData<List<StockRecord>>

    @Query("SELECT * FROM stock_record WHERE accountId = :accountId ORDER BY transactionDate")
    fun getStockRecordsByAccountId(accountId: Int): LiveData<List<StockRecord>>

    @Query("SELECT * FROM stock_record ORDER BY transactionDate")
    fun getAllStockRecords(): LiveData<List<StockRecord>>

    @Query("SELECT COUNT(*) FROM stock_record WHERE accountId = :accountId")
    suspend fun getRecordCountByAccountId(accountId: Int): Int

    @Query("DELETE FROM stock_record WHERE accountId = :accountId")
    suspend fun deleteAllRecordsByAccountId(accountId: Int)

    @Query("SELECT MIN(transactionDate) FROM stock_record WHERE accountId = :accountId")
    suspend fun getMinTransactionDateByAccountId(accountId: Int): Long?

    @Query("SELECT MAX(transactionDate) FROM stock_record WHERE accountId = :accountId")
    suspend fun getMaxTransactionDateByAccountId(accountId: Int): Long?
}