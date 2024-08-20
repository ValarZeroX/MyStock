package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.repository.StockAccountRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal

class StockAccountViewModel(private val repository: StockAccountRepository) : ViewModel() {
    val stockAccounts: LiveData<List<StockAccount>> = repository.getAllStockAccounts()
    val stockAccountsMap: LiveData<Map<Int, StockAccount>> = repository.getAllStockAccountsMap()

    val firstStockAccount: LiveData<StockAccount?> = repository.getFirstStockAccount()

    fun insertStockAccount(
        accountName: String,
        currencyCode: String,
        stockMarket: Int,
        autoCalculate: Boolean,
        commissionDecimal: Double,
        transactionTaxDecimal: Double,
        discount: Double
    ) {
        viewModelScope.launch {
            val stockAccount = StockAccount(
                account = accountName,
                currency = currencyCode,
                stockMarket = stockMarket,
                autoCalculate = autoCalculate,
                commissionDecimal = commissionDecimal,
                transactionTaxDecimal = transactionTaxDecimal,
                discount = discount,
                accountSort = 0,
                transactionTaxDecimalETF = 0.001,
                transactionTaxDecimalDayTrading = 0.0015,
                commissionWholeLot = 0.0,
                commissionOddLot = 0.0,
            )
            repository.insertStockAccount(stockAccount)
        }
    }

    fun getStockAccountByID(accountId: Int): LiveData<StockAccount?> {
        return repository.getStockAccountByID(accountId)
    }

    fun updateStockAccountOrder(newOrder: List<StockAccount>) = viewModelScope.launch {
        newOrder.forEachIndexed { index, stockAccount ->
            stockAccount.accountSort = index
        }
        repository.updateAll(newOrder)
    }

    fun deleteStockAccountById(accountId: Int) {
        viewModelScope.launch {
            repository.deleteStockAccountById(accountId)
        }
    }

    fun updateStockAccount(stockAccount: StockAccount) {
        viewModelScope.launch {
            repository.updateStockAccount(stockAccount)
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