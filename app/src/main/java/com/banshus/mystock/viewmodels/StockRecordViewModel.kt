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

//    fun getHoldingsAndTotalCost(accountId: Int): LiveData<Pair<Map<String, Pair<Int, Double>>, Double>> {
//        return repository.getStockRecordsByAccountId(accountId).map { stockRecords ->
//            val holdings = stockRecords.groupBy { it.stockSymbol }
//                .mapValues { (_, records) ->
//                    val totalQuantity = records.sumOf { record ->
//                        when (record.transactionType) {
//                            0 -> record.quantity // 0: 買入
//                            1 -> -record.quantity // 1: 賣出
//                            else -> 0
//                        }
//                    }
//                    val totalValue = records.sumOf { record ->
//                        when (record.transactionType) {
//                            0 -> record.totalAmount // 0: 買入
//                            1 -> -record.totalAmount // 1: 賣出
//                            else -> 0.0
//                        }
//                    }
//                    totalQuantity to totalValue
//                }
//
//            // Calculate total cost
//            val totalCost = holdings.values.sumOf { (_, totalValue) -> totalValue }
//
//            holdings to totalCost
//        }
//    }

//    fun getCurrentHoldings(accountId: Int): LiveData<Map<String, Pair<Int, Double>>> {
//        return repository.getStockRecordsByAccountId(accountId).map { stockRecords ->
//            stockRecords.groupBy { it.stockSymbol }
//                .mapValues { (_, records) ->
//                    val totalQuantity = records.sumOf { record ->
//                        when (record.transactionType) {
//                            0 -> record.quantity // 0: 買入
//                            1 -> -record.quantity // 1: 賣出
//                            else -> 0
//                        }
//                    }
//                    val totalValue = records.sumOf { record ->
//                        when (record.transactionType) {
//                            0 -> record.totalAmount // 0: 買入
//                            1 -> -record.totalAmount // 1: 賣出
//                            else -> 0.0
//                        }
//                    }
//                    totalQuantity to totalValue
//                }
//        }
//    }
//
//    //帳戶總成本
//    fun getTotalCost(accountId: Int): LiveData<Double> {
//        return getCurrentHoldings(accountId).map { holdings ->
//            holdings.values.sumOf { (_, totalValue) -> totalValue }
//        }
//    }
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