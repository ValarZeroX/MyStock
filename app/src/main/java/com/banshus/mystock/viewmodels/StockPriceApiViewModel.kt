package com.banshus.mystock.viewmodels

import android.util.Log
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

    private val _error = MutableLiveData<String?>()
//    val error: LiveData<String?> get() = _error

//    fun fetchStockPrice(symbol: String, period1: Long, period2: Long) {
//        viewModelScope.launch {
//            try {
//                val response = repository.getStockPrice(symbol, period1, period2)
//                if (response.chart.error != null) {
//                    _error.value = response.chart.error.description
////                    _stockPrice.value = null // Clear the stock price if there is an error
//                } else {
//                    _stockPrice.value = response
//                    _error.value = null
//                }
//            } catch (e: Exception) {
//                _error.value = "Unable to fetch stock price. Please try again later."
//                Log.e("StockPriceApiViewModel", "Error fetching stock price", e)
//            }
//        }
//    }

    fun fetchStockPriceResult(symbol: String, period1: Long, period2: Long, marketCode: String, onSuccess: (StockChartResponse) -> Unit) {
        viewModelScope.launch {
            try {
                val combinedSymbol:String = if (marketCode != "US") {
                    "${symbol}.${marketCode}"
                } else {
                    symbol
                }
                val response = repository.getStockPrice(combinedSymbol, period1, period2)
                if (response.chart.error != null) {
                    _error.value = response.chart.error.description
//                    _stockPrice.value = null // Clear the stock price if there is an error
                } else {
                    _stockPrice.value = response
                    onSuccess(response)
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Unable to fetch stock price. Please try again later."
                Log.e("StockPriceApiViewModel", "Error fetching stock price", e)
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