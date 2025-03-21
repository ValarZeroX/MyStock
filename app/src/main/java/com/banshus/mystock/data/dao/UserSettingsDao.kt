package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.data.entities.UserSettings

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings")
    fun getUserSettings(): LiveData<UserSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userSettings: UserSettings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(userSettings: List<UserSettings>)

    @Update
    suspend fun update(userSettings: UserSettings)

    @Query("SELECT * FROM user_settings LIMIT 1")
    suspend fun getUserSettingsSync(): UserSettings
}