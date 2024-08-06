package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.repository.StockSymbolRepository
import kotlinx.coroutines.launch

class StockSymbolViewModel(private val repository: StockSymbolRepository) : ViewModel() {
    private val _stockSymbolsListByMarket = MutableLiveData<List<StockSymbol>>()
    val stockSymbolsListByMarket: LiveData<List<StockSymbol>> = _stockSymbolsListByMarket

    fun fetchStockSymbolsListByMarket(stockMarket: Int) {
        viewModelScope.launch {
            val stockSymbols = repository.fetchStockSymbolsListByMarket(stockMarket)
            _stockSymbolsListByMarket.value = stockSymbols
        }
    }


//    fun insertStockSymbol(stockSymbol: String, stockName: String, stockMarket: Int) {
//        viewModelScope.launch {
//            val stockSymbolEntity = StockSymbol(
//                stockSymbol = stockSymbol,
//                stockName = stockName,
//                stockMarket = stockMarket
//            )
//            repository.insertStockSymbol(stockSymbolEntity)
//            fetchStockSymbolsListByMarket(stockMarket)
//        }
//    }

    fun insertStockSymbol(stockSymbol: StockSymbol) = viewModelScope.launch {
        repository.insertStockSymbol(stockSymbol)
        fetchStockSymbolsListByMarket(stockSymbol.stockMarket)
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
