package com.banshus.mystock.csv

import com.banshus.mystock.data.entities.*

fun StockRecord.toCSVRow(): String {
    return "$recordId,$accountId,$stockMarket,$stockSymbol,$stockType,$transactionType," +
            "$transactionDate,$quantity,$pricePerUnit,$totalAmount,$commission,$transactionTax,$note"
}

fun StockSymbol.toCSVRow(): String {
    return "$stockSymbol,$stockName,$stockMarket,$stockPrice,$regularMarketDayLow," +
            "$regularMarketDayHigh,$chartPreviousClose,$lastUpdatedTime"
}

fun StockAccount.toCSVRow(): String {
    return "$accountId,$account,$currency,$stockMarket,$autoCalculate,$commissionDecimal," +
            "$transactionTaxDecimal,$discount,$accountSort,$transactionTaxDecimalETF," +
            "$transactionTaxDecimalDayTrading,$commissionWholeLot,$commissionOddLot"
}

fun Currency.toCSVRow(): String {
    return "$id,$currencyCode,$exchangeRate,$lastUpdatedTime"
}

fun StockMarket.toCSVRow(): String {
    return "$stockMarket,$stockMarketName,$stockMarketCode,$stockMarketSort"
}

fun UserSettings.toCSVRow(): String {
    return "$id,$themeIndex,$isCommissionCalculationEnabled,$isTransactionTaxCalculationEnabled," +
            "$isDividendCalculationEnabled,$currency,$textColor,$autoUpdateStock,$autoUpdateStockSecond," +
            "$autoUpdateExchangeRate,$autoUpdateExchangeRateSecond"
}