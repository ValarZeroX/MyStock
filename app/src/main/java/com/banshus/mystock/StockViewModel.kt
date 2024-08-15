package com.banshus.mystock

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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

        private val _selectedReportTabIndex = mutableIntStateOf(0)
        val selectedReportTabIndex: MutableState<Int> = _selectedReportTabIndex

//        fun setSelectedTabIndex(index: Int) {
//                _selectedTabIndex.intValue = index
//        }

        // 這是用來控制顯示的狀態
        private val _showRangeTypeDialog = MutableLiveData(false)
        val showRangeTypeDialog: LiveData<Boolean> = _showRangeTypeDialog

        // 方法用來顯示或隱藏對話框
        fun showDialog() {
                _showRangeTypeDialog.value = true
        }

        fun hideDialog() {
                _showRangeTypeDialog.value = false
        }
}