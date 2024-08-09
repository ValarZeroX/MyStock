package com.banshus.mystock.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.api.response.StockDayAllTwResponse
import com.banshus.mystock.repository.TwseApiRepository
import kotlinx.coroutines.launch

class TwseApiViewModel(private val repository: TwseApiRepository) : ViewModel() {
    private val _stockDayData = MutableLiveData<List<StockDayAllTwResponse>>()
    val stockDayData: LiveData<List<StockDayAllTwResponse>> get() = _stockDayData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchStockDayAll() {
        viewModelScope.launch {
            try {
                val response = repository.getStockDayAll()
                _stockDayData.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "无法获取股票日数据，请稍后重试。"
                Log.e("TwseApiViewModel", "Error fetching stock day data", e)
            }
        }
    }
}

class TwseApiViewModelFactory(private val repository: TwseApiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TwseApiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TwseApiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}