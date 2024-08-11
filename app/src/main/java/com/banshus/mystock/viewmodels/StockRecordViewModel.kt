package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.repository.StockRecordRepository
import com.banshus.mystock.repository.StockSymbolRepository
import kotlinx.coroutines.launch

data class RealizedResult(
    val buyCost: Double,       // 买入成本
    val sellIncome: Double,    // 卖出收入
    val dividendIncome: Double, // 股利收入
    val totalCommission: Double, // 手续费总和
    val totalTransactionTax: Double // 交易税总和
)

class StockRecordViewModel(
    private val repository: StockRecordRepository
) : ViewModel() {
    fun getStockRecordsByDateRangeAndAccount(
        accountId: Int,
        startDate: Long,
        endDate: Long
    ): LiveData<List<StockRecord>> {
        return repository.getStockRecordsByDateRangeAndAccount(accountId, startDate, endDate)
    }

    fun insertStockRecord(stockRecord: StockRecord) {
        viewModelScope.launch {
            repository.insertStockRecord(stockRecord)
        }
    }

//    fun getStockRecordsByAccountId(accountId: Int): LiveData<List<StockRecord>> {
//        return repository.getStockRecordsByAccountId(accountId)
//    }

    fun getHoldingsAndTotalCost(accountId: Int): LiveData<Pair<Map<String, Pair<Int, Double>>, Double>> {
        return repository.getStockRecordsByAccountId(accountId).map { stockRecords ->
            val holdings = stockRecords.groupBy { it.stockSymbol }
                .mapValues { (_, records) ->
                    var totalQuantity = 0
                    var costBasis = 0.0

                    val buyRecords = mutableListOf<Pair<Int, Double>>()

                    for (record in records) {
                        when (record.transactionType) {
                            0 -> {
                                totalQuantity += record.quantity
                                buyRecords.add(record.quantity to (record.totalAmount / record.quantity))
                                costBasis += record.totalAmount
                            }

                            1 -> {
                                if (totalQuantity > 0) {
                                    var quantityToSell = record.quantity

                                    while (quantityToSell > 0 && buyRecords.isNotEmpty()) {
                                        val (buyQuantity, buyPrice) = buyRecords.removeAt(0)
                                        val quantityToRemove = minOf(quantityToSell, buyQuantity)
                                        quantityToSell -= quantityToRemove
                                        totalQuantity -= quantityToRemove
                                        costBasis -= quantityToRemove * buyPrice
                                        if (buyQuantity > quantityToRemove) {
                                            buyRecords.add(
                                                0,
                                                (buyQuantity - quantityToRemove) to buyPrice
                                            )
                                        }
                                    }
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
        return repository.getStockRecordsByAccountId(accountId).map { stockRecords ->
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
                        0 -> { // 买入
                            buyRecords.add(record.quantity to (record.totalAmount / record.quantity))
                        }
                        1 -> { // 卖出
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

                            realizedExpenditure += totalCostForThisSell // 卖出的对应买入成本
                            realizedIncome += record.totalAmount // 卖出收入
                        }
                        2 -> { // 股利
                            dividendIncome += record.quantity * record.pricePerUnit // 股利收入
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
}

class StockRecordViewModelFactory(
    private val repository: StockRecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}