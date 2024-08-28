package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.Currency
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.repository.CurrencyRepository
import kotlinx.coroutines.launch

class CurrencyViewModel(private val repository: CurrencyRepository) : ViewModel() {

//    private val _allCurrencies = MutableLiveData<List<Currency>>()
//    val allCurrencies: LiveData<List<Currency>> = _allCurrencies

    val allCurrencies: LiveData<List<Currency>> = repository.fetchAllCurrencies()
//    fun fetchAllCurrencies() {
//        viewModelScope.launch {
//            val currencies = repository.fetchAllCurrencies()
//            _allCurrencies.value = currencies
//        }
//    }

    fun insertCurrency(currency: Currency) = viewModelScope.launch {
        repository.insertCurrency(currency)
//        fetchAllCurrencies() // 插入新货币后刷新列表
    }

    fun updateCurrency(currency: Currency) = viewModelScope.launch {
        repository.updateCurrency(currency)
//        fetchAllCurrencies() // 更新货币后刷新列表
    }
}

class CurrencyViewModelFactory(
    private val repository: CurrencyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}