package com.banshus.mystock.repository

import androidx.lifecycle.LiveData
import com.banshus.mystock.data.dao.CurrencyDao
import com.banshus.mystock.data.entities.Currency

class CurrencyRepository(private val currencyDao: CurrencyDao) {

    // 获取所有的货币和汇率
    fun fetchAllCurrencies(): LiveData<List<Currency>> {
        return currencyDao.getAllCurrencies()
    }

    // 插入新的货币记录
    suspend fun insertCurrency(currency: Currency) {
        currencyDao.insertCurrency(currency)
    }

    // 更新已有的货币记录
    suspend fun updateCurrency(currency: Currency) {
        currencyDao.updateCurrency(currency)
    }
}