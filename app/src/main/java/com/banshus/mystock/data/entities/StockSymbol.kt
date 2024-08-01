package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_symbol",
    indices = [Index(value = ["stockSymbolId"])]
)
data class StockSymbol(
    @PrimaryKey(autoGenerate = true) val stockSymbolId: Int = 0,
    val stockSymbol: String, // 股票代碼
    val stockName: String, // 股票名稱
    var stockType: String, //股票分類(台股、美股)
)
