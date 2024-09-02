package com.banshus.mystock.csv

import android.content.Context
import com.banshus.mystock.data.database.AppDatabase

import java.io.File

suspend fun exportCSV(context: Context): File {
    val database = AppDatabase.getDatabase(context)

    // 获取所有的数据
    val stockRecords = database.stockRecordDao().getAllStockRecordsSync()
    val stockSymbols = database.stockSymbolDao().getAllStockSymbolsSync()
    val stockAccounts = database.stockAccountDao().getAllStockAccountsSync()
    val currencies = database.currencyDao().getAllCurrenciesSync()
    val stockMarkets = database.stockMarketDao().getAllStockMarketsSync()
    val userSettings = database.userSettingsDao().getUserSettingsSync()

    // 构建 CSV 内容
    val csvContent = StringBuilder()

    // StockRecord部分
    csvContent.append("recordId,accountId,stockMarket,stockSymbol,stockType,transactionType," +
            "transactionDate,quantity,pricePerUnit,totalAmount,commission,transactionTax,note\n")
    stockRecords.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // StockSymbol部分
    csvContent.append("stockSymbol,stockName,stockMarket,stockPrice,regularMarketDayLow,regularMarketDayHigh,chartPreviousClose,lastUpdatedTime\n")
    stockSymbols.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // StockAccount部分
    csvContent.append("accountId,account,currency,stockMarket,autoCalculate,commissionDecimal,transactionTaxDecimal," +
            "discount,accountSort,transactionTaxDecimalETF,transactionTaxDecimalDayTrading,commissionWholeLot,commissionOddLot\n")
    stockAccounts.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // Currency部分
    csvContent.append("id,currencyCode,exchangeRate,lastUpdatedTime\n")
    currencies.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // StockMarket部分
    csvContent.append("stockMarket,stockMarketName,stockMarketCode,stockMarketSort\n")
    stockMarkets.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // UserSettings部分
    csvContent.append("id,themeIndex,darkTheme,isCommissionCalculationEnabled,isTransactionTaxCalculationEnabled,isDividendCalculationEnabled," +
            "currency,textColor,autoUpdateStock,autoUpdateStockSecond,autoUpdateExchangeRate,autoUpdateExchangeRateSecond\n")
    csvContent.append(userSettings.toCSVRow())

    // 写入文件
    val csvFile = File(context.getExternalFilesDir(null), "skynier_data.csv")
    csvFile.writeText(csvContent.toString())
    return csvFile
}