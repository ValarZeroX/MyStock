package com.banshus.mystock.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_account")
data class StockAccount(
    @PrimaryKey(autoGenerate = true) val accountId: Int = 0,
    var account: String,
    val currency: String
)
