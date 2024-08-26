package com.banshus.mystock.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.banshus.mystock.data.dao.StockRecordDao
import com.banshus.mystock.data.entities.StockRecord
import androidx.lifecycle.map
import com.banshus.mystock.NumberUtils.formatNumberNoDecimalPointDouble

data class RealizedResult(
    val buyCost: Double,       // 买入成本
    val sellIncome: Double,    // 卖出收入
    val dividendIncome: Double, // 股利收入
    val totalCommission: Double, // 手续费总和
    val totalTransactionTax: Double // 交易税总和
)

data class RealizedTrade(
    val buy: List<StockRecord>,  // 买入记录
    val sell: StockRecord         // 卖出记录
)

class StockRecordRepository(private val stockRecordDao: StockRecordDao) {

    suspend fun insertStockRecord(stockRecord: StockRecord) {
        stockRecordDao.insertStockRecord(stockRecord)
    }

    suspend fun updateStockRecord(stockRecord: StockRecord) {
        stockRecordDao.updateStockRecord(stockRecord)
    }

    suspend fun deleteStockRecordById(recordId: Int) {
        stockRecordDao.deleteStockRecordById(recordId)
    }

    suspend fun getRecordCountByAccountId(accountId: Int): Int {
        return stockRecordDao.getRecordCountByAccountId(accountId)
    }

    suspend fun deleteAllRecordsByAccountId(accountId: Int) {
        stockRecordDao.deleteAllRecordsByAccountId(accountId)
    }

    suspend fun getMinTransactionDateByAccountId(accountId: Int): Long? {
        return stockRecordDao.getMinTransactionDateByAccountId(accountId)
    }

    suspend fun getMaxTransactionDateByAccountId(accountId: Int): Long? {
        return stockRecordDao.getMaxTransactionDateByAccountId(accountId)
    }

    fun getStockRecordsByDateRangeAndAccount(
        accountId: Int,
        startDate: Long,
        endDate: Long
    ): LiveData<List<StockRecord>> {
        return stockRecordDao.getStockRecordsByDateRangeAndAccount(accountId, startDate, endDate)
    }

    fun getStockRecordsByDateRange(
    startDate: Long,
    endDate: Long
    ): LiveData<List<StockRecord>> {
        return stockRecordDao.getStockRecordsByDateRange(startDate, endDate)
    }

    private fun getStockRecordsByAccountId(accountId: Int): LiveData<List<StockRecord>> {
        return stockRecordDao.getStockRecordsByAccountId(accountId)
    }

    private fun getAllStockRecords(): LiveData<List<StockRecord>> {
        return stockRecordDao.getAllStockRecords()
    }

    fun getHoldingsAndTotalCost(accountId: Int): LiveData<Pair<Map<String, Pair<Int, Double>>, Double>> {
        return getStockRecordsByAccountId(accountId).map { stockRecords ->
            val holdings = stockRecords.groupBy { it.stockSymbol }
                .mapValues { (_, records) ->
                    var totalQuantity = 0
                    var costBasis = 0.0

                    val buyRecords =
                        mutableListOf<Pair<Int, Double>>() // Pair of quantity and price

                    for (record in records) {
                        when (record.transactionType) {
                            0 -> { // Buy
                                totalQuantity += record.quantity
                                buyRecords.add(record.quantity to (record.totalAmount / record.quantity))
                                costBasis += record.totalAmount
                            }

                            1 -> { // Sell
                                if (totalQuantity > 0) {
                                    var quantityToSell = record.quantity
                                    var totalCostForThisSell = 0.0

                                    while (quantityToSell > 0 && buyRecords.isNotEmpty()) {
                                        val (buyQuantity, buyPrice) = buyRecords.removeAt(0)
                                        val quantityToRemove = minOf(quantityToSell, buyQuantity)
                                        quantityToSell -= quantityToRemove
                                        totalQuantity -= quantityToRemove
                                        totalCostForThisSell += quantityToRemove * buyPrice

                                        if (buyQuantity > quantityToRemove) {
                                            buyRecords.add(
                                                0,
                                                (buyQuantity - quantityToRemove) to buyPrice
                                            )
                                        }
                                    }

                                    costBasis -= totalCostForThisSell
                                }
                            }
                        }
                    }

                    totalQuantity to costBasis
                }

            val totalCost = holdings.values.sumOf { (_, costBasis) -> costBasis }

            holdings to totalCost
        }
    }

    fun getRealizedGainsAndLosses(accountId: Int): LiveData<Map<String, RealizedResult>> {
        return getStockRecordsByAccountId(accountId).map { stockRecords ->
            val results = mutableMapOf<String, RealizedResult>()

            stockRecords.groupBy { it.stockSymbol }.forEach { (stockSymbol, records) ->
                var realizedIncome = 0.0
                var realizedExpenditure = 0.0
                var dividendIncome = 0.0
                var totalCommission = 0.0
                var totalTransactionTax = 0.0

                val buyRecords = mutableListOf<Pair<Int, Double>>() // Pair of quantity and price

                for (record in records) {
                    totalCommission += record.commission
                    totalTransactionTax += record.transactionTax

                    when (record.transactionType) {
                        0 -> { // Buy
                            buyRecords.add(record.quantity to (record.totalAmount / record.quantity))
                        }

                        1 -> { // Sell
                            var quantityToSell = record.quantity
                            var totalCostForThisSell = 0.0

                            while (quantityToSell > 0 && buyRecords.isNotEmpty()) {
                                val (buyQuantity, buyPrice) = buyRecords.removeAt(0)
                                val quantityToRemove = minOf(quantityToSell, buyQuantity)
                                quantityToSell -= quantityToRemove
                                totalCostForThisSell += quantityToRemove * buyPrice

                                if (buyQuantity > quantityToRemove) {
                                    buyRecords.add(0, (buyQuantity - quantityToRemove) to buyPrice)
                                }
                            }

                            realizedExpenditure += totalCostForThisSell // Cost of sold shares
                            realizedIncome += record.totalAmount // Selling income
                        }

                        2 -> { // Dividend
                            dividendIncome += record.quantity * record.pricePerUnit // Dividend income
                        }
                    }
                }

                results[stockSymbol] = RealizedResult(
                    buyCost = realizedExpenditure,
                    sellIncome = realizedIncome,
                    dividendIncome = dividendIncome,
                    totalCommission = totalCommission,
                    totalTransactionTax = totalTransactionTax
                )
            }

            results
        }
    }

    fun getHoldingsAndTotalCostForAllAccounts(): LiveData<Map<Int, Pair<Map<String, Pair<Int, Double>>, Double>>> {
        return getAllStockRecords().map { stockRecords ->
            val results = stockRecords.groupBy { it.accountId }
                .mapValues { (_, accountRecords) ->
                    val holdings = accountRecords.groupBy { it.stockSymbol }
                        .mapValues { (_, records) ->
                            var totalQuantity = 0
                            var costBasis = 0.0
                            val buyRecords = mutableListOf<Pair<Int, Double>>()

                            for (record in records) {
                                when (record.transactionType) {
                                    0 -> { // Buy
                                        totalQuantity += record.quantity
                                        buyRecords.add(record.quantity to (record.totalAmount / record.quantity))
                                        costBasis += record.totalAmount
                                    }

                                    1 -> { // Sell
                                        if (totalQuantity > 0) {
                                            var quantityToSell = record.quantity
                                            var totalCostForThisSell = 0.0

                                            while (quantityToSell > 0 && buyRecords.isNotEmpty()) {
                                                val (buyQuantity, buyPrice) = buyRecords.removeAt(0)
                                                val quantityToRemove =
                                                    minOf(quantityToSell, buyQuantity)
                                                quantityToSell -= quantityToRemove
                                                totalQuantity -= quantityToRemove
                                                totalCostForThisSell += quantityToRemove * buyPrice

                                                if (buyQuantity > quantityToRemove) {
                                                    buyRecords.add(
                                                        0,
                                                        (buyQuantity - quantityToRemove) to buyPrice
                                                    )
                                                }
                                            }

                                            costBasis -= totalCostForThisSell
                                        }
                                    }
                                }
                            }
                            totalQuantity to costBasis
                        }

                    val totalCost = holdings.values.sumOf { (_, costBasis) -> costBasis }
                    holdings to totalCost
                }
            results
        }
    }

    fun getRealizedTradesForAllAccounts(): LiveData<Map<Int, Map<String, List<RealizedTrade>>>> {
        return getAllStockRecords().map { stockRecords ->
            val results = mutableMapOf<Int, MutableMap<String, MutableList<RealizedTrade>>>()

            stockRecords.groupBy { it.accountId }.forEach { (accountId, accountRecords) ->
                accountRecords.groupBy { it.stockSymbol }.forEach { (stockSymbol, records) ->
                    val realizedTrades = mutableListOf<RealizedTrade>()
                    val buyRecords = mutableListOf<StockRecord>()

                    records.sortedBy { it.transactionDate }.forEach { record ->
                        when (record.transactionType) {
                            0 -> { // 买入
                                buyRecords.add(record)
                            }
                            1 -> { // 卖出
                                var quantityToSell = record.quantity
                                val matchedBuys = mutableListOf<StockRecord>()

                                val iterator = buyRecords.iterator()
                                while (iterator.hasNext() && quantityToSell > 0) {
                                    val buyRecord = iterator.next()
                                    val availableQuantity = buyRecord.quantity
                                    val quantityToMatch = minOf(quantityToSell, availableQuantity)

                                    matchedBuys.add(
                                        buyRecord.copy(
                                            quantity = quantityToMatch,
                                            totalAmount = quantityToMatch * buyRecord.pricePerUnit
                                        )
                                    )

                                    quantityToSell -= quantityToMatch
                                    buyRecord.quantity -= quantityToMatch

                                    if (buyRecord.quantity == 0) {
                                        iterator.remove()
                                    }
                                }

                                realizedTrades.add(RealizedTrade(buy = matchedBuys, sell = record))
                            }
                        }
                    }

                    if (realizedTrades.isNotEmpty()) {
                        if (!results.containsKey(accountId)) {
                            results[accountId] = mutableMapOf()
                        }
                        results[accountId]!![stockSymbol] = realizedTrades
                    }
                }
            }

            results
        }
    }

    // ***************

    fun getRealizedGainsAndLossesForAllAccounts(
        startDate: Long,
        endDate: Long
    ): LiveData<Map<Int, Map<String, Any>>> {
        return MutableLiveData<Map<Int, Map<String, Any>>>().apply {
            getAllStockRecords().observeForever { stockRecords ->
                val filteredSellRecords = stockRecords.filter { it.transactionType == 1 }
                    .groupBy { it.accountId }
                val filteredBuyRecords = stockRecords.filter { it.transactionType == 0 }
                    .groupBy { it.accountId }
                val data = calculateRealizedGainsAndLosses(filteredBuyRecords, filteredSellRecords)
                Log.d("data", "$data")
                value = data
            }
        }
    }

    private fun calculateRealizedGainsAndLosses(
        buyRecords: Map<Int, List<StockRecord>>,
        sellRecords: Map<Int, List<StockRecord>>
    ): Map<Int, Map<String, Any>> {
        val realizedGainsAndLosses = mutableMapOf<Int, MutableMap<String, Any>>()

        // Process only the accounts present in sellRecords
        sellRecords.forEach { (accountId, sells) ->
            val buys = buyRecords[accountId] ?: emptyList()
            val realizedResults = mutableMapOf<String, Any>()

            val realized = matchAndCalculate(buys, sells)

            realizedResults["realized"] = realized

            realizedGainsAndLosses[accountId] = realizedResults
        }

        return realizedGainsAndLosses
    }

    private fun matchAndCalculate(
        buyRecords: List<StockRecord>,
        sellRecords: List<StockRecord>
    ): Map<Int, Map<String, List<StockRecord>>> {
        val realized = mutableMapOf<Int, Map<String, List<StockRecord>>>()

        // 将买入记录按时间排序
        val currentBuyRecords = buyRecords.sortedBy { it.transactionDate }.toMutableList()

        // 按时间排序卖出记录
        sellRecords.sortedBy { it.transactionDate }.forEach { sell ->
            var quantityToSell = sell.quantity
            // 临时存储处理的买入和卖出记录
            val buyToProcess = mutableListOf<StockRecord>()
            val sellToProcess = mutableListOf<StockRecord>()

            // 处理买入记录
            val iterator = currentBuyRecords.iterator()
            while (iterator.hasNext() && quantityToSell > 0) {
                val buy = iterator.next()
                if (buy.stockSymbol == sell.stockSymbol) {  // 确保股票符号匹配
                    if (buy.quantity > 0) {
                        val quantity = minOf(buy.quantity, quantityToSell)
                        buyToProcess.add(buy.copy(quantity = quantity, totalAmount = quantity * buy.pricePerUnit))
                        sellToProcess.add(sell.copy(quantity = quantity, totalAmount = quantity * sell.pricePerUnit))
                        quantityToSell -= quantity
                        buy.quantity -= quantity
                        // 如果买入记录的数量为0，移除该记录
                        if (buy.quantity == 0) {
                            iterator.remove()
                        }
                    }
                }
            }

            if (buyToProcess.isNotEmpty() || sellToProcess.isNotEmpty()) {
                val id = sell.recordId
                realized[id] = mapOf(
                    "buy" to buyToProcess.toList(),  // 确保转换为 List<StockRecord>
                    "sell" to sellToProcess.toList() // 确保转换为 List<StockRecord>
                )
            }
        }
        return realized
    }

    //********
    fun getRealizedGainsAndLossesWithAllocatedCommissionForAllAccounts(): LiveData<Map<Int, Map<String, List<RealizedTrade>>>> {
        return getAllStockRecords().map { stockRecords ->
            val results = mutableMapOf<Int, MutableMap<String, List<RealizedTrade>>>()

            // 按 accountId 进行分类
            stockRecords.groupBy { it.accountId }.forEach { (accountId, records) ->
                val realizedTrades = mutableMapOf<String, List<RealizedTrade>>()

                // 按 stockSymbol 分类
                records.groupBy { it.stockSymbol }.forEach { (stockSymbol, symbolRecords) ->
                    val buyRecords = mutableListOf<StockRecord>()
                    val realizedTradesForSymbol = mutableListOf<RealizedTrade>()

                    symbolRecords.forEach { record ->
                        when (record.transactionType) {
                            0 -> { // 买入
                                buyRecords.add(record)
                            }
                            1 -> { // 卖出
                                var quantityToSell = record.quantity
                                val currentSell = record.copy(quantity = quantityToSell)
                                val buysForThisSell = mutableListOf<StockRecord>()

                                while (quantityToSell > 0 && buyRecords.isNotEmpty()) {
                                    val buy = buyRecords.removeAt(0)
                                    val quantitySold = minOf(buy.quantity, quantityToSell)
                                    val allocatedCommission = formatNumberNoDecimalPointDouble(buy.commission * quantitySold / buy.quantity)

                                    val buyForSell = buy.copy(quantity = quantitySold, commission = allocatedCommission)
                                    buysForThisSell.add(buyForSell)

                                    buy.quantity -= quantitySold
                                    quantityToSell -= quantitySold

                                    if (buy.quantity > 0) {
                                        buyRecords.add(0, buy)
                                    }
                                }

                                if (buysForThisSell.isNotEmpty()) {
                                    realizedTradesForSymbol.add(
                                        RealizedTrade(
                                            buy = buysForThisSell,
                                            sell = currentSell
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (realizedTradesForSymbol.isNotEmpty()) {
                        realizedTrades[stockSymbol] = realizedTradesForSymbol
                    }
                }

                results[accountId] = realizedTrades
            }

            results
        }
    }

    fun getDividendRecordsByDateRangeAndAccount(
        accountId: Int,
        startDate: Long,
        endDate: Long
    ): LiveData<List<StockRecord>> {
        return stockRecordDao.getDividendRecordsByDateRangeAndAccount(accountId, startDate, endDate)
    }
}