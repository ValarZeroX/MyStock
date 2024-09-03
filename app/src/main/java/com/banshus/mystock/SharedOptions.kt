package com.banshus.mystock

import android.content.Context

object SharedOptions {
    fun getOptionsTransactionType(context: Context) = listOf(
        context.getString(R.string.transaction_type_buy),
        context.getString(R.string.transaction_type_sell),
        context.getString(R.string.transaction_type_dividend)
    )

    fun getOptionsStockType(context: Context) = listOf(
        context.getString(R.string.stock_type_normal),
        context.getString(R.string.stock_type_etf)
    )

    fun getOptionStockMarket(context: Context) = listOf(
        context.getString(R.string.taiwan_stocks),
        context.getString(R.string.us_stocks),
        context.getString(R.string.hk_stocks),
    )

    fun getPriceName(context: Context, selectedTransactionType: Int): String {
        return when (selectedTransactionType) {
            0, 1 -> context.getString(R.string.price_per_share)
            else -> context.getString(R.string.price_per_dividend)
        }
    }

    val currencyCodes = listOf(
        "AUD", "BRL", "CAD", "CHF", "CZK", "DKK", "EUR", "GBP", "HKD", "HUF",
        "ILS", "JPY", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RMB", "RUB",
        "SEK", "SGD", "THB", "TRY", "TWD", "USD"
    )
}