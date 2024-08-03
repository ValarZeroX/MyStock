package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "stock_account")
data class StockAccount(
    @PrimaryKey(autoGenerate = true) val accountId: Int = 0,
    var account: String,
    val currency: String,
    val stockMarket: Int = 0, //股票市場 0:台股,1:美股
    val autoCalculate: Boolean = false, //自動計算成本
    val commissionDecimal: Double = 0.001425, // 手續費率
    var transactionTaxDecimal: Double = 0.003, //證交稅率
    var discount: Double = 1.0, //水續費折扣
)
