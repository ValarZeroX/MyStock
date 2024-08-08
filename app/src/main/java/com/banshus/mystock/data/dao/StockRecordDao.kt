package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.banshus.mystock.data.entities.StockRecord

@Dao
interface StockRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockRecord(stockRecord: StockRecord)

    @Query("SELECT * FROM stock_record WHERE accountId = :accountId AND transactionDate BETWEEN :startDate AND :endDate")
    fun getStockRecordsByDateRangeAndAccount(
        accountId: Int,
        startDate: Long,
        endDate: Long
    ): LiveData<List<StockRecord>>

    @Query("SELECT * FROM stock_record WHERE accountId = :accountId")
    fun getStockRecordsByAccountId(accountId: Int): LiveData<List<StockRecord>>
}