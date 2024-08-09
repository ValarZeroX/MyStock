package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.repository.StockRecordRepository
import kotlinx.coroutines.launch

class StockRecordViewModel(private val repository: StockRecordRepository) : ViewModel() {
    fun getStockRecordsByDateRangeAndAccount(accountId: Int, startDate: Long, endDate: Long): LiveData<List<StockRecord>> {
        return repository.getStockRecordsByDateRangeAndAccount(accountId, startDate, endDate)
    }

    fun insertStockRecord(accountId: Int, stockMarket: Int, stockSymbol: String, stockType: Int, transactionType: Int, transactionDate: Long, quantity: Int, pricePerUnit: Double, totalAmount: Double, commission: Double, transactionTax: Double, note: String) {
        viewModelScope.launch {
            val stockRecord = StockRecord(
                accountId = accountId,
                stockMarket = stockMarket,
                stockSymbol = stockSymbol,
                stockType = stockType,
                transactionType = transactionType,
                transactionDate = transactionDate,
                quantity = quantity,
                pricePerUnit = pricePerUnit,
                totalAmount = totalAmount,
                commission = commission,
                transactionTax = transactionTax,
                note = note
            )
            repository.insertStockRecord(stockRecord)
        }
    }

    fun getStockRecordsByAccountId(accountId: Int): LiveData<List<StockRecord>> {
        return repository.getStockRecordsByAccountId(accountId)
    }

    fun getCurrentHoldings(accountId: Int): LiveData<Map<String, Pair<Int, Double>>> {
        return repository.getStockRecordsByAccountId(accountId).map { stockRecords ->
            stockRecords.groupBy { it.stockSymbol }
                .mapValues { (_, records) ->
                    val totalQuantity = records.sumOf { record ->
                        when (record.transactionType) {
                            0 -> record.quantity // 0: 買入
                            1 -> -record.quantity // 1: 賣出
                            else -> 0
                        }
                    }
                    val totalValue = records.sumOf { record ->
                        when (record.transactionType) {
                            0 -> record.totalAmount // 0: 買入
                            1 -> -record.totalAmount // 1: 賣出
                            else -> 0.0
                        }
                    }
                    totalQuantity to totalValue
                }
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