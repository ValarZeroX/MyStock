package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
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
    fun getAllCurrencies(): LiveData<List<Currency>>

    @Query("SELECT * FROM stock_currency")
    suspend fun getAllCurrenciesSync(): List<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<Currency>)
}