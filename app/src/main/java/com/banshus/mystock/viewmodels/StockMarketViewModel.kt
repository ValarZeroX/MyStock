package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.repository.StockMarketRepository
import kotlinx.coroutines.launch

class StockMarketViewModel(private val repository: StockMarketRepository) : ViewModel() {
    val allStockMarkets: LiveData<List<StockMarket>> = repository.allStockMarkets

    fun insert(stockMarket: StockMarket) = viewModelScope.launch {
        repository.insert(stockMarket)
    }

    fun update(stockMarket: StockMarket) = viewModelScope.launch {
        repository.update(stockMarket)
    }

    fun delete(stockMarket: StockMarket) = viewModelScope.launch {
        repository.delete(stockMarket)
    }

    fun updateStockMarketsOrder(newOrder: List<StockMarket>) = viewModelScope.launch {
        newOrder.forEachIndexed { index, stockMarket ->
            println(stockMarket)
            stockMarket.stockMarketSort = index
        }
        repository.updateAll(newOrder)
    }

    suspend fun getStockMarketById(stockMarket: Int): StockMarket? {
        return repository.getStockMarketById(stockMarket)
    }
}

class StockMarketViewModelFactory(private val repository: StockMarketRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockMarketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockMarketViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}