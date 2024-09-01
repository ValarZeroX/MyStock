package com.banshus.mystock.csv

import android.content.Context
import android.net.Uri
import android.util.Log
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.Currency
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.data.entities.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun importDataFromCSV(context: Context, csvFileUri: Uri) {
    withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(csvFileUri)?.bufferedReader()?.use { reader ->
            val lines = reader.readLines()
            var index = 0

            while (index < lines.size) {
                val line = lines[index]
                when {
                    line.startsWith("recordId") -> {
                        index = importStockRecords(context, lines, index)
                    }
                    line.startsWith("stockSymbol") -> {
                        index = importStockSymbols(context, lines, index)
                    }
                    line.startsWith("accountId") && !line.contains("recordId") -> {
                        index = importStockAccounts(context, lines, index)
                    }
                    line.startsWith("id") && line.contains("currencyCode") -> {
                        index = importCurrencies(context, lines, index)
                    }
                    line.startsWith("stockMarket") && line.contains("stockMarketName") -> {
                        index = importStockMarkets(context, lines, index)
                    }
                    line.startsWith("id") && line.contains("themeIndex") -> {
                        index = importUserSettings(context, lines, index)
                    }
                    else -> index++
                }
            }
        }
    }
}

private suspend fun importStockRecords(context: Context, lines: List<String>, startIndex: Int): Int {
    val stockRecords = mutableListOf<StockRecord>()
    var index = startIndex + 1

    while (index < lines.size && !lines[index].startsWith("stockSymbol")) {
        val tokens = lines[index].split(",").map { it.trim() }
        Log.d("tokens", "$tokens")
        Log.d("size", "${tokens.size}")
        if (tokens.size == 13) {
            val stockRecord = StockRecord(
                recordId = tokens[0].toInt(),
                accountId = tokens[1].toInt(),
                stockMarket = tokens[2].toInt(),
                stockSymbol = tokens[3],
                stockType = tokens[4].toInt(),
                transactionType = tokens[5].toInt(),
                transactionDate = tokens[6].toLong(),
                quantity = tokens[7].toInt(),
                pricePerUnit = tokens[8].toDouble(),
                totalAmount = tokens[9].toDouble(),
                commission = tokens[10].toDouble(),
                transactionTax = tokens[11].toDouble(),
                note = tokens[12]
            )
            Log.d("stockRecord", "$stockRecord")
            stockRecords.add(stockRecord)
        }
        index++
    }
    Log.d("stockRecords", "$stockRecords")
    val database = AppDatabase.getDatabase(context)
    val stockRecordDao = database.stockRecordDao()
    stockRecordDao.insertStockRecords(stockRecords)

    return index
}

private suspend fun importStockSymbols(context: Context, lines: List<String>, startIndex: Int): Int {
    val stockSymbols = mutableListOf<StockSymbol>()
    var index = startIndex + 1

    while (index < lines.size && !lines[index].startsWith("accountId")) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 8) {
            val stockSymbol = StockSymbol(
                stockSymbol = tokens[0],
                stockName = tokens[1],
                stockMarket = tokens[2].toInt(),
                stockPrice = tokens[3].toDoubleOrNull(),
                regularMarketDayLow = tokens[4].toDoubleOrNull(),
                regularMarketDayHigh = tokens[5].toDoubleOrNull(),
                chartPreviousClose = tokens[6].toDoubleOrNull(),
                lastUpdatedTime = tokens[7].toLongOrNull()
            )
            stockSymbols.add(stockSymbol)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    val stockSymbolDao = database.stockSymbolDao()
    stockSymbolDao.insertStockSymbols(stockSymbols)

    return index
}

private suspend fun importStockAccounts(context: Context, lines: List<String>, startIndex: Int): Int {
    val stockAccounts = mutableListOf<StockAccount>()
    var index = startIndex + 1

    while (index < lines.size && !lines[index].startsWith("id")) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 13) {
            val stockAccount = StockAccount(
                accountId = tokens[0].toInt(),
                account = tokens[1],
                currency = tokens[2],
                stockMarket = tokens[3].toInt(),
                autoCalculate = tokens[4].toBoolean(),
                commissionDecimal = tokens[5].toDouble(),
                transactionTaxDecimal = tokens[6].toDouble(),
                discount = tokens[7].toDouble(),
                accountSort = tokens[8].toInt(),
                transactionTaxDecimalETF = tokens[9].toDouble(),
                transactionTaxDecimalDayTrading = tokens[10].toDouble(),
                commissionWholeLot = tokens[11].toDouble(),
                commissionOddLot = tokens[12].toDouble()
            )
            stockAccounts.add(stockAccount)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    val stockAccountDao = database.stockAccountDao()
    stockAccountDao.insertStockAccounts(stockAccounts)

    return index
}

private suspend fun importCurrencies(context: Context, lines: List<String>, startIndex: Int): Int {
    val currencies = mutableListOf<Currency>()
    var index = startIndex + 1

    while (index < lines.size && !lines[index].startsWith("stockMarket")) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 4) {
            val currency = Currency(
                id = tokens[0].toInt(),
                currencyCode = tokens[1],
                exchangeRate = tokens[2].toDouble(),
                lastUpdatedTime = tokens[3].toLongOrNull()
            )
            currencies.add(currency)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    val currencyDao = database.currencyDao()
    currencyDao.insertCurrencies(currencies)

    return index
}

private suspend fun importStockMarkets(context: Context, lines: List<String>, startIndex: Int): Int {
    val stockMarkets = mutableListOf<StockMarket>()
    var index = startIndex + 1

    while (index < lines.size && !lines[index].startsWith("id")) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 4) {
            val stockMarket = StockMarket(
                stockMarket = tokens[0].toInt(),
                stockMarketName = tokens[1],
                stockMarketCode = tokens[2],
                stockMarketSort = tokens[3].toInt()
            )
            stockMarkets.add(stockMarket)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    val stockMarketDao = database.stockMarketDao()
    stockMarketDao.insertStockMarkets(stockMarkets)

    return index
}

private suspend fun importUserSettings(context: Context, lines: List<String>, startIndex: Int): Int {
    val userSettingsList = mutableListOf<UserSettings>()
    var index = startIndex + 1

    while (index < lines.size) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 11) {
            val userSettings = UserSettings(
                id = tokens[0].toInt(),
                themeIndex = tokens[1].toInt(),
                isCommissionCalculationEnabled = tokens[2].toBoolean(),
                isTransactionTaxCalculationEnabled = tokens[3].toBoolean(),
                isDividendCalculationEnabled = tokens[4].toBoolean(),
                currency = tokens[5],
                textColor = tokens[6].toInt(),
                autoUpdateStock = tokens[7].toBoolean(),
                autoUpdateStockSecond = tokens[8].toInt(),
                autoUpdateExchangeRate = tokens[9].toBoolean(),
                autoUpdateExchangeRateSecond = tokens[10].toInt()
            )
            userSettingsList.add(userSettings)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    val userSettingsDao = database.userSettingsDao()
    userSettingsDao.insertUserSettings(userSettingsList)

    return index
}