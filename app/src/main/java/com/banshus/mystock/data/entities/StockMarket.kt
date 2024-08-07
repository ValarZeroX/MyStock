package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_market",
)
data class StockMarket(
    @PrimaryKey(autoGenerate = true) val stockMarket: Int = 0,
    val stockMarketName: String, // 股票市場名稱
    val stockMarketCode: String, //股票市場代碼
    var stockMarketSort: Int, //股票市場排序
)