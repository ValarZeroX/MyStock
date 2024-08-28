package com.banshus.mystock

object SharedOptions {
    val optionsTransactionType = listOf("買入", "賣出", "股利")
    val optionsStockType = listOf("一般", "ETF")
    val optionStockMarket = listOf("台股", "美股")

    val currencyCodes = listOf(
        "AUD", "BRL", "CAD", "CHF", "CZK", "DKK", "EUR", "GBP", "HKD", "HUF",
        "ILS", "JPY", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RMB", "RUB",
        "SEK", "SGD", "THB", "TRY", "TWD", "USD"
    )
}