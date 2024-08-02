package com.banshus.mystock.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.banshus.mystock.data.dao.StockAccountDao
import com.banshus.mystock.data.dao.UserSettingsDao
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserSettings::class, StockAccount::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun stockAccountDao(): StockAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database")
//                    .createFromAsset("database/stock.db")
                    .addCallback(DatabaseCallback())
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
                        populateDatabase(database.userSettingsDao())
                    }
                }
            }

            suspend fun populateDatabase(userSettingsDao: UserSettingsDao) {
                // 插入预设值
                val defaultSettings = listOf(
                    UserSettings(themeIndex = 0)
                )
                defaultSettings.forEach { userSettingsDao.insert(it) }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 创建新的表
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `stock_account` (
                        `accountId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `account` TEXT NOT NULL, 
                        `currency` TEXT NOT NULL)"""
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 添加新的字段到现有表
                db.execSQL(
                    "ALTER TABLE `stock_account` ADD COLUMN `stockMarket` INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

    }
}