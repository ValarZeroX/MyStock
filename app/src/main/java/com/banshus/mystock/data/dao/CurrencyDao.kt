package com.banshus.mystock.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banshus.mystock.data.entities.Currency

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: Currency)

    @Update
    suspend fun updateCurrency(currency: Currency)

    @Query("SELECT * FROM stock_currency")
    fun getAllCurrencies(): List<Currency>
}