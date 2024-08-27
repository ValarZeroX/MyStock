package com.banshus.mystock.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.banshus.mystock.data.dao.CurrencyDao
import com.banshus.mystock.data.dao.StockAccountDao
import com.banshus.mystock.data.dao.StockMarketDao
import com.banshus.mystock.data.dao.UserSettingsDao
import com.banshus.mystock.data.dao.StockRecordDao
import com.banshus.mystock.data.dao.StockSymbolDao
import com.banshus.mystock.data.entities.Currency
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.data.entities.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserSettings::class, StockAccount::class, StockRecord::class, StockSymbol::class, StockMarket::class, Currency::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun stockAccountDao(): StockAccountDao
    abstract fun stockRecordDao(): StockRecordDao
    abstract fun stockSymbolDao(): StockSymbolDao
    abstract fun stockMarketDao(): StockMarketDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
//            deleteDatabase(context)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database")
//                    .createFromAsset("database/stock.db")
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance

                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.userSettingsDao(), database.stockMarketDao())
                    }
                }
            }

            suspend fun populateDatabase(userSettingsDao: UserSettingsDao, stockMarketDao: StockMarketDao) {
                // 插入预设值
                val defaultSettings = listOf(
                    UserSettings(themeIndex = 0)
                )
                defaultSettings.forEach { userSettingsDao.insert(it) }

                val defaultStockMarkets = listOf(
                    StockMarket(stockMarket = 0, stockMarketName = "台股", stockMarketCode = "TW", stockMarketSort = 0),
                    StockMarket(stockMarket = 1, stockMarketName = "美股", stockMarketCode = "US", stockMarketSort = 1)
                )
                defaultStockMarkets.forEach { stockMarketDao.insertStockMarket(it) }
            }
        }

        private fun deleteDatabase(context: Context) {
            context.deleteDatabase("app_database")
        }
    }
}

