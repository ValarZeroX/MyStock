package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_record",
    foreignKeys = [
        ForeignKey(
            entity = StockSymbol::class,
            parentColumns = ["stockSymbolId"],
            childColumns = ["stockSymbolId"],
            onDelete = ForeignKey.CASCADE // 可选：关联删除
        ),
        ForeignKey(
            entity = StockAccount::class,
            parentColumns = ["accountId"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE // 可选：关联删除
        )
    ],
    indices = [Index(value = ["stockSymbolId"]), Index(value = ["accountId"])]
)
data class StockRecord(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0,
    val accountId: Int, // 關聯的帳戶ID
    val stockMarket: Int, //股票市場 0:台股,1:美股
    val stockSymbolId: String, // 股票資訊
    val stockName: String, // 股票名稱
    val stockType: Int, // 股票類別(一般或ETF)
    val transactionType: Int, // 交易類型（0:買入或1:賣出 2:股利）
    val transactionDate: Long, // 交易日期（時間戳記）
    val quantity: Double, // 股數
    val pricePerUnit: Double, // 每單位價格
    val totalAmount: Double, // 成交價格
    val commission: Double, // 手續費
    var transactionTax: Double, //交易稅
    var note: String //備註
)
