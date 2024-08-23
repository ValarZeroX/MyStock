package com.banshus.mystock.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import java.time.Instant
import java.time.ZoneId

data class StockMetrics(
    val totalCostBasis: Double,
    val totalPrice: Double,
    val totalProfit: Double,
    val totalProfitPercent: Double
)

data class DetailedStockMetrics(
    val totalCostBasis: Double,        // 总买入成本
    val totalSellIncome: Double,       // 总卖出收入
    val totalProfit: Double,           // 总利润
    val totalProfitPercent: Double,    // 总利润百分比
    val totalCommission: Double,       // 总手续费
    val totalTransactionTax: Double    // 总交易税
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

    fun getDateSerialNumberMapByDateRange(
        startDate: Long,
        endDate: Long
    ): LiveData<Map<Int, Int>> {
        val result = MediatorLiveData<Map<Int, Int>>()

        val recordsLiveData = repository.getStockRecordsByDateRange(startDate, endDate)

        result.addSource(recordsLiveData) { records ->
            val distinctDates = records.map { record ->
                // 提取日期中的日（例如23日）
                record.transactionDate.let {
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .dayOfMonth
                }
            }.distinct() // 去除重复的日期

            val dateSerialNumberMap = distinctDates.mapIndexed { index, day ->
                // 生成键值对：键为流水号，值为日期中的日
                index + 1 to day
            }.toMap()

            result.value = dateSerialNumberMap
        }

        return result
    }

    fun getStockRecordsByDateRange(
        startDate: Long,
        endDate: Long
    ): LiveData<List<StockRecord>> {
        return repository.getStockRecordsByDateRange(startDate, endDate)
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

    fun getRecordCountByAccountId(accountId: Int): LiveData<Int> {
        return liveData {
            emit(repository.getRecordCountByAccountId(accountId))
        }
    }

    fun getTransactionDateRangeByAccountId(accountId: Int): LiveData<Pair<Long?, Long?>> = liveData {
        val minDate = repository.getMinTransactionDateByAccountId(accountId)
        val maxDate = repository.getMaxTransactionDateByAccountId(accountId)
        emit(Pair(minDate, maxDate))
    }

    fun deleteAllRecordsByAccountId(accountId: Int) {
        viewModelScope.launch {
            repository.deleteAllRecordsByAccountId(accountId)
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
    private fun getRealizedGainsAndLossesWithAllocatedCommissionForAllAccounts(): LiveData<Map<Int, Map<String, List<RealizedTrade>>>> {
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

    fun calculateMetricsForSelectedAccount(
        startDate: Long,
        endDate: Long,
        accountId: Int
    ): LiveData<DetailedStockMetrics> {
        val filteredTrades = getFilteredRealizedTrades(startDate, endDate)

        return filteredTrades.map { allTrades ->
            val trades = allTrades[accountId] ?: emptyMap()

            var totalBuyCost = 0.0
            var totalSellIncome = 0.0
            var totalProfit = 0.0
            var totalCommission = 0.0
            var totalTransactionTax = 0.0

            // 迭代所有交易记录
            trades.forEach { (stockSymbol, realizedTrades) ->
                realizedTrades.forEach { trade ->
                    // 累计买入成本
                    trade.buy.forEach { buyRecord ->
                        totalBuyCost += buyRecord.quantity * buyRecord.pricePerUnit
                        totalCommission += buyRecord.commission
                        totalTransactionTax += buyRecord.transactionTax
                    }

                    // 累计卖出收入
                    val sellIncome = trade.sell.quantity * trade.sell.pricePerUnit
                    totalSellIncome += sellIncome

                    // 累计卖出的手续费和交易税
                    totalCommission += trade.sell.commission
                    totalTransactionTax += trade.sell.transactionTax

                    // 计算该次交易的利润，并累加到总利润
                    val profit = sellIncome - (trade.buy.sumOf { it.quantity * it.pricePerUnit })
                    totalProfit += profit
                }
            }

            // 计算利润百分比
            val totalProfitPercent = if (totalBuyCost != 0.0) {
                (totalProfit / totalBuyCost) * 100
            } else {
                0.0
            }

            DetailedStockMetrics(
                totalCostBasis = totalBuyCost,
                totalSellIncome = totalSellIncome,
                totalProfit = totalProfit,
                totalProfitPercent = totalProfitPercent,
                totalCommission = totalCommission,
                totalTransactionTax = totalTransactionTax
            )
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