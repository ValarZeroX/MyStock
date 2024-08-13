package com.banshus.mystock

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockRecord

class StockViewModel : ViewModel() {
        private val _selectedAccount = MutableLiveData<StockAccount?>()
        val selectedAccount: LiveData<StockAccount?> = _selectedAccount

        fun updateSelectedAccount(account: StockAccount) {
                _selectedAccount.value = account
        }

        private val _selectedAccountForStockList = MutableLiveData<StockAccount?>()
        val selectedAccountForStockList: LiveData<StockAccount?> = _selectedAccountForStockList

        fun updateSelectedAccountForStockList(account: StockAccount) {
                _selectedAccountForStockList.value = account
        }

        private val _selectedStock  = MutableLiveData<StockRecord?>()
        val selectedStock : LiveData<StockRecord?> = _selectedStock
        fun updateSelectedStock(stock: StockRecord) {
                _selectedStock.value = stock
        }

        private val _selectedTabIndex = mutableIntStateOf(0)
        val selectedTabIndex: MutableState<Int> = _selectedTabIndex

        fun setSelectedTabIndex(index: Int) {
                _selectedTabIndex.intValue = index
        }
}