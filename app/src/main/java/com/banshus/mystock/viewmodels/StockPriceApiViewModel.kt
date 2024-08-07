package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.api.response.StockChartResponse
import com.banshus.mystock.repository.StockPriceApiRepository
import kotlinx.coroutines.launch


class StockPriceApiViewModel(private val repository: StockPriceApiRepository) : ViewModel() {
    private val _stockPrice = MutableLiveData<StockChartResponse>()
    val stockPrice: LiveData<StockChartResponse> get() = _stockPrice

    fun fetchStockPrice(symbol: String, period1: Long, period2: Long) {
        viewModelScope.launch {
            try {
                val response = repository.getStockPrice(symbol, period1, period2)
                _stockPrice.value = response
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

class StockPriceApiViewModelFactory(private val repository: StockPriceApiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockPriceApiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockPriceApiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}