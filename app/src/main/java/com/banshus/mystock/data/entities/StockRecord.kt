package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_record",
    indices = [Index(value = ["accountId"])]
)
data class StockRecord(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0,
    val accountId: Int, // 關聯的帳戶ID
    val stockMarket: Int, //股票市場 0:台股,1:美股
    val stockSymbol: String, // 股票代碼
    val stockType: Int, // 股票類別(一般或ETF)
    val transactionType: Int, // 交易類型（0:買入或1:賣出 2:股利）
    val transactionDate: Long, // 交易日期（時間戳記）
    var quantity: Int, // 股數
    val pricePerUnit: Double, // 每單位價格
    var totalAmount: Double, // 成交價格
    val commission: Double, // 手續費
    var transactionTax: Double, //交易稅
    var note: String //備註
)
