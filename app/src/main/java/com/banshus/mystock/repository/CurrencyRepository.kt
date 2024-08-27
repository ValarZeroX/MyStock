package com.banshus.mystock.repository

import com.banshus.mystock.data.dao.CurrencyDao
import com.banshus.mystock.data.entities.Currency

class CurrencyRepository(private val currencyDao: CurrencyDao) {

    // 获取所有的货币和汇率
    suspend fun fetchAllCurrencies(): List<Currency> {
        return currencyDao.getAllCurrencies() // 如果你使用 LiveData，可以修改为直接返回 List<Currency>
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