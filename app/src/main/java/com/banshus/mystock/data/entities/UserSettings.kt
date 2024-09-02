package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.banshus.mystock.ui.stock.Currency

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val themeIndex: Int = 0, //主題顏色
    val darkTheme: Boolean = true,
    val isCommissionCalculationEnabled: Boolean = false, //損益是否包含手續費
    val isTransactionTaxCalculationEnabled: Boolean = false, //損益是否包含證交稅
    val isDividendCalculationEnabled: Boolean = false, //損益是否包含股利
    val currency: String = "USD", //主幣別
    val textColor: Int = 0, //漲跌文字顏色
    val autoUpdateStock: Boolean = false, //是否自動更新股價
    val autoUpdateStockSecond: Int = 180, //自動更新股價(秒)
    val autoUpdateExchangeRate: Boolean = false, //是否自動更新匯率
    val autoUpdateExchangeRateSecond: Int = 180, //自動更新匯率(秒)
)

