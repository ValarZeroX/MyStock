package com.banshus.mystock.repository

import androidx.lifecycle.LiveData
import com.banshus.mystock.data.dao.StockAccountDao
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.UserSettings

class StockAccountRepository(private val stockAccountDao: StockAccountDao) {
    fun getAllStockAccounts(): LiveData<List<StockAccount>> {
        return stockAccountDao.getAllStockAccounts()
    }
    suspend fun insertStockAccount(stockAccount: StockAccount) {
        stockAccountDao.insert(stockAccount)
    }

    fun getFirstStockAccount(): LiveData<StockAccount?> {
        return stockAccountDao.getFirstStockAccount()
    }

    fun getStockAccountByID(accountId: Int): LiveData<StockAccount?> {
        return stockAccountDao.getStockAccountByID(accountId)
    }
}