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
    var transactionTaxDecimal: Double = 0.003, //交易稅
    var discount: Double = 1.0, //水續費折扣
    var accountSort: Int, //帳號排序
    var transactionTaxDecimalETF: Double = 0.001, //ETF交易稅
    var transactionTaxDecimalDayTrading: Double = 0.0015, //當沖交易稅
    var commissionWholeLot: Double = 0.0, //整股最低消費
    var commissionOddLot: Double = 0.0, //零股最低消費
)
