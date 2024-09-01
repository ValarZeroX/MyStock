package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.repository.StockSymbolRepository
import kotlinx.coroutines.launch

class StockSymbolViewModel(private val repository: StockSymbolRepository) : ViewModel() {
    private val _stockSymbolsListByMarket = MutableLiveData<List<StockSymbol>>()
    val stockSymbolsListByMarket: LiveData<List<StockSymbol>> = _stockSymbolsListByMarket

    private val _allStockSymbols = MutableLiveData<List<StockSymbol>>()
    val allStockSymbols: LiveData<List<StockSymbol>> = _allStockSymbols


    fun fetchStockSymbolsListByMarket(stockMarket: Int) {
        viewModelScope.launch {
            val stockSymbols = repository.fetchStockSymbolsListByMarket(stockMarket)
            _stockSymbolsListByMarket.value = stockSymbols
        }
    }

    fun fetchAllStockSymbols() {
        viewModelScope.launch {
            val stockSymbols = repository.fetchAllStockSymbols()
            _allStockSymbols.value = stockSymbols
        }
    }

    fun insertStockSymbol(stockSymbol: StockSymbol) = viewModelScope.launch {
        repository.insertStockSymbol(stockSymbol)
        fetchStockSymbolsListByMarket(stockSymbol.stockMarket)
    }

    fun updateStockName(symbol: String, marketId: Int, newName: String) = viewModelScope.launch {
        repository.updateStockName(symbol, marketId, newName)
        fetchStockSymbolsListByMarket(marketId)
    }
}

class StockSymbolViewModelFactory(
    private val repository: StockSymbolRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockSymbolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockSymbolViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
