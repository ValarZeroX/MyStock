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
    val stockMarket: Int, //股票分類(台股、美股)
    val stockPrice: Double? = null, // 股票價格
    val regularMarketDayLow: Double? = null, //最低價格
    val regularMarketDayHigh: Double? = null, //最高價格
    val chartPreviousClose: Double? = null, //昨天收盤價
    val lastUpdatedTime: Long? = null // 股票價格最後更新時間
)
