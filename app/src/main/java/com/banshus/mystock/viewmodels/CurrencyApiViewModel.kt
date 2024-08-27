package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.api.response.CurrencyRatesResponse
import com.banshus.mystock.repository.CurrencyApiRepository
import kotlinx.coroutines.launch

class CurrencyApiViewModel (private val repository: CurrencyApiRepository) : ViewModel() {

    private val _currencyRates = MutableLiveData<CurrencyRatesResponse?>()
    val currencyRates: LiveData<CurrencyRatesResponse?> get() = _currencyRates

    fun fetchCurrencyRates() {
        viewModelScope.launch {
            val rates = repository.fetchCurrencyRates()
            _currencyRates.value = rates
        }
    }
}

class CurrencyApiViewModelFactory(private val repository: CurrencyApiRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyApiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyApiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}