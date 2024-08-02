package com.banshus.mystock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.repository.StockAccountRepository
import kotlinx.coroutines.launch

class StockAccountViewModel(private val repository: StockAccountRepository) : ViewModel() {
    fun insertStockAccount(accountName: String, currencyCode: String, stockMarket: Int) {
        viewModelScope.launch {
            val stockAccount = StockAccount(account = accountName, currency = currencyCode, stockMarket = stockMarket)
            repository.insertStockAccount(stockAccount)
        }
    }
}

class StockAccountViewModelFactory(
    private val repository: StockAccountRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockAccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockAccountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}