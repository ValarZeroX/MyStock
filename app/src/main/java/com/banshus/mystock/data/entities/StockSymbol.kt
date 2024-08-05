package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_symbol",
    indices = [Index(value = ["stockSymbol", "stockMarket"], unique = true)]
)
data class StockSymbol(
    @PrimaryKey val stockSymbol: String, // 股票代碼
    val stockName: String, // 股票名稱
    var stockMarket: Int, //股票分類(台股、美股)
)
