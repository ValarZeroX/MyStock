package com.banshus.mystock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.banshus.mystock.data.entities.StockAccount

class StockViewModel() : ViewModel() {
//        private val _selectedAccount = MutableLiveData<String>()
//        val selectedAccount: LiveData<String> = _selectedAccount
//
//        fun updateSelectedAccount(account: String) {
//                _selectedAccount.value = account
//        }

        private val _selectedAccount = MutableLiveData<StockAccount?>()
        val selectedAccount: LiveData<StockAccount?> = _selectedAccount

        fun updateSelectedAccount(account: StockAccount) {
                _selectedAccount.value = account
        }
}