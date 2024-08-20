package com.banshus.mystock.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.banshus.mystock.data.dao.StockAccountDao
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.UserSettings

class StockAccountRepository(private val stockAccountDao: StockAccountDao) {
    fun getAllStockAccounts(): LiveData<List<StockAccount>> {
        return stockAccountDao.getAllStockAccounts()
    }
    fun getAllStockAccountsMap(): LiveData<Map<Int, StockAccount>> {
        return stockAccountDao.getAllStockAccounts().map { stockAccounts ->
            stockAccounts.associateBy { it.accountId }
        }
    }
    suspend fun insertStockAccount(stockAccount: StockAccount) {
        stockAccountDao.insert(stockAccount)
    }

    suspend fun deleteStockAccountById(accountId: Int) {
        stockAccountDao.deleteStockAccountById(accountId)
    }

    fun getFirstStockAccount(): LiveData<StockAccount?> {
        return stockAccountDao.getFirstStockAccount()
    }

    fun getStockAccountByID(accountId: Int): LiveData<StockAccount?> {
        return stockAccountDao.getStockAccountByID(accountId)
    }

    suspend fun updateAll(stockAccounts: List<StockAccount>) {
        stockAccountDao.updateAll(stockAccounts)
    }

    suspend fun updateStockAccount(stockAccount: StockAccount) {
        stockAccountDao.updateStockAccount(stockAccount)
    }
}