package com.banshus.mystock.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.repository.RealizedResult
import com.banshus.mystock.repository.RealizedTrade
import com.banshus.mystock.repository.StockRecordRepository
import com.banshus.mystock.repository.StockSymbolRepository
import kotlinx.coroutines.launch

data class StockMetrics(
    val totalCostBasis: Double,
    val totalPrice: Double,
    val totalProfit: Double,
    val totalProfitPercent: Double
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

    fun updateStockRecord(stockRecord: StockRecord) {
        viewModelScope.launch {
            repository.updateStockRecord(stockRecord)
        }
    }

    fun deleteStockRecordById(recordId: Int) {
        viewModelScope.launch {
            repository.deleteStockRecordById(recordId)
        }
    }

    fun getHoldingsAndTotalCost(accountId: Int): LiveData<Pair<Map<String, Pair<Int, Double>>, Double>> {
        return repository.getHoldingsAndTotalCost(accountId)
    }

    fun getRealizedGainsAndLosses(accountId: Int): LiveData<Map<String, RealizedResult>> {
        return repository.getRealizedGainsAndLosses(accountId)
    }

    private val _stockSymbols = mutableStateOf<List<StockSymbol>>(emptyList())
    private val stockSymbols: State<List<StockSymbol>> = _stockSymbols

    fun setStockSymbols(symbols: List<StockSymbol>) {
        _stockSymbols.value = symbols
    }

    fun calculateTotalCostAndProfit(accountId: Int): LiveData<StockMetrics> {
        return getHoldingsAndTotalCost(accountId).map { (holdings, _) ->
            val totalCostBasis = holdings.values.sumOf { (_, costBasis) -> costBasis }

            val totalPrice = holdings.entries.sumOf { (stockSymbol, holdingData) ->
                val (totalQuantity, _) = holdingData
                val currentPrice = stockSymbols.value.find { it.stockSymbol == stockSymbol }?.stockPrice ?: 0.0
                totalQuantity * currentPrice
            }

            val totalProfit = totalPrice - totalCostBasis
            val totalProfitPercent = if (totalCostBasis != 0.0) {
                (totalProfit / totalCostBasis) * 100
            } else {
                0.0
            }

            StockMetrics(totalCostBasis, totalPrice, totalProfit, totalProfitPercent)
        }
    }

    fun calculateTotalCostAndProfitForAllAccounts(): LiveData<Map<Int, StockMetrics>> {
        return repository.getHoldingsAndTotalCostForAllAccounts().map { holdingsByAccount ->
            holdingsByAccount.mapValues { (_, holdingsAndTotalCost) ->
                val (holdings, _) = holdingsAndTotalCost

                val totalCostBasis = holdings.values.sumOf { (_, costBasis) -> costBasis }
                val totalPrice = holdings.entries.sumOf { (stockSymbol, holdingData) ->
                    val (totalQuantity, _) = holdingData
                    val currentPrice = stockSymbols.value.find { it.stockSymbol == stockSymbol }?.stockPrice ?: 0.0
                    totalQuantity * currentPrice
                }
                val totalProfit = totalPrice - totalCostBasis
                val totalProfitPercent = if (totalCostBasis != 0.0) {
                    (totalProfit / totalCostBasis) * 100
                } else {
                    0.0
                }

                StockMetrics(totalCostBasis, totalPrice, totalProfit, totalProfitPercent)
            }
        }
    }

    fun getRealizedTradesForAllAccounts(): LiveData<Map<Int, Map<String, List<RealizedTrade>>>> {
        return repository.getRealizedTradesForAllAccounts()
    }

    //***********************

    private val _realizedGainsAndLossesForAllAccounts = MutableLiveData<Map<Int, Map<String, Any>>>()
    val realizedGainsAndLossesForAllAccounts: LiveData<Map<Int, Map<String, Any>>> = _realizedGainsAndLossesForAllAccounts

    fun loadRealizedGainsAndLossesForAllAccounts(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            repository.getRealizedGainsAndLossesForAllAccounts(startDate, endDate).observeForever { result ->
                // 更新 LiveData
                _realizedGainsAndLossesForAllAccounts.postValue(result)
            }
        }
    }

    //**************************
    fun getRealizedGainsAndLossesWithAllocatedCommissionForAllAccounts(): LiveData<Map<Int, Map<String, List<RealizedTrade>>>> {
        return repository.getRealizedGainsAndLossesWithAllocatedCommissionForAllAccounts()
    }

    fun getFilteredRealizedTrades(
        startDate: Long,
        endDate: Long
    ): LiveData<Map<Int, Map<String, List<RealizedTrade>>>> {
        val allRealizedTrades = getRealizedGainsAndLossesWithAllocatedCommissionForAllAccounts()

        return allRealizedTrades.map { allTrades ->
            allTrades.mapValues { (_, stockTrades) ->
                stockTrades.mapValues { (_, realizedTrades) ->
                    realizedTrades.filter { trade ->
                        trade.sell.transactionDate in startDate..endDate
                    }
                }.filterValues { it.isNotEmpty() }
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