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
import retrofit2.HttpException


class StockPriceApiViewModel(private val repository: StockPriceApiRepository) : ViewModel() {
    private val _stockPrice = MutableLiveData<StockChartResponse>()
    val stockPrice: LiveData<StockChartResponse> get() = _stockPrice

    private val _error = MutableLiveData<String?>()
    fun fetchStockPriceResult(
        symbol: String,
        period1: Long,
        period2: Long,
        marketCode: String,
        onSuccess: (StockChartResponse?) -> Unit,
        onError: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val combinedSymbol: String = if (marketCode != "US") {
                    "${symbol}.${marketCode}"
                } else {
                    symbol
                }

                val response = repository.getStockPrice(combinedSymbol, period1, period2)
                if (response?.chart?.error != null) {
                    val errorDescription = response.chart.error.description
                    _error.value = errorDescription
                    onError(errorDescription)
                } else {
//                _stockPrice.value = response.chart.result.
                    onSuccess(response)
                    _error.value = null
                }
            } catch (e: HttpException) {
                Log.d("error002", "$e")
                Log.d("error002", "${e.response()}")
                Log.d("error002", e.message())
                if (e.code() == 404) {
                    val errorMessage = "沒有找到該股票的數據: $symbol"
                    _error.value = errorMessage
                    onError(errorMessage)
                } else {
                    val errorMessage = "由於網路錯誤，無法取得股票資訊。"
                    _error.value = errorMessage
                    onError(errorMessage)
                }
                Log.e("StockPriceApiViewModel", "HTTP error fetching stock price", e)
            } catch (e: Exception) {
                val errorMessage = "股票代碼錯誤。無法取得股票資訊。請稍後重試。"
                _error.value = errorMessage
                onError(errorMessage)
                Log.e("StockPriceApiViewModel", "General error fetching stock price", e)
            }
        }
    }
}

class StockPriceApiViewModelFactory(private val repository: StockPriceApiRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockPriceApiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockPriceApiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}