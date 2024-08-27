package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_currency")
data class Currency(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val currencyCode: String, // 货币代码，例如 USD, EUR
    val exchangeRate: Double   // 汇率，例如 1.0, 0.85
)
