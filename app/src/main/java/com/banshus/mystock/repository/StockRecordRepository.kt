package com.banshus.mystock.repository

import androidx.lifecycle.LiveData
import com.banshus.mystock.data.dao.StockRecordDao
import com.banshus.mystock.data.entities.StockRecord
import androidx.lifecycle.map

data class RealizedResult(
    val buyCost: Double,       // 买入成本
    val sellIncome: Double,    // 卖出收入
    val dividendIncome: Double, // 股利收入
    val totalCommission: Double, // 手续费总和
    val totalTransactionTax: Double // 交易税总和
)

class StockRecordRepository(private val stockRecordDao: StockRecordDao) {

    suspend fun insertStockRecord(stockRecord: StockRecord) {
        stockRecordDao.insertStockRecord(stockRecord)
    }

    fun getStockRecordsByDateRangeAndAccount(accountId: Int, startDate: Long, endDate: Long): LiveData<List<StockRecord>> {
        return stockRecordDao.getStockRecordsByDateRangeAndAccount(accountId, startDate, endDate)
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

                    val buyRecords = mutableListOf<Pair<Int, Double>>() // Pair of quantity and price

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
                                            buyRecords.add(0, (buyQuantity - quantityToRemove) to buyPrice)
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
                                                val quantityToRemove = minOf(quantityToSell, buyQuantity)
                                                quantityToSell -= quantityToRemove
                                                totalQuantity -= quantityToRemove
                                                totalCostForThisSell += quantityToRemove * buyPrice

                                                if (buyQuantity > quantityToRemove) {
                                                    buyRecords.add(0, (buyQuantity - quantityToRemove) to buyPrice)
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

    fun getRealizedGainsAndLossesForAllAccounts(): LiveData<Map<Int, Map<String, RealizedResult>>> {
        return getAllStockRecords().map { stockRecords ->
            val results = stockRecords.groupBy { it.accountId }
                .mapValues { (_, accountRecords) ->
                    accountRecords.groupBy { it.stockSymbol }.mapValues { (_, records) ->
                        var realizedIncome = 0.0
                        var realizedExpenditure = 0.0
                        var dividendIncome = 0.0
                        var totalCommission = 0.0
                        var totalTransactionTax = 0.0

                        val buyRecords = mutableListOf<Pair<Int, Double>>()

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

                                    realizedExpenditure += totalCostForThisSell
                                    realizedIncome += record.totalAmount
                                }
                                2 -> { // Dividend
                                    dividendIncome += record.quantity * record.pricePerUnit
                                }
                            }
                        }

                        RealizedResult(
                            buyCost = realizedExpenditure,
                            sellIncome = realizedIncome,
                            dividendIncome = dividendIncome,
                            totalCommission = totalCommission,
                            totalTransactionTax = totalTransactionTax
                        )
                    }
                }
            results
        }
    }
}