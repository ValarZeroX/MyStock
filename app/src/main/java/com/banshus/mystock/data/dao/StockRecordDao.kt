package com.banshus.mystock.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.banshus.mystock.data.entities.StockRecord

@Dao
interface StockRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockRecord(stockRecord: StockRecord)
}