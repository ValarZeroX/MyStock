package com.banshus.mystock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banshus.mystock.data.entities.UserSettings

interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 0 LIMIT 1")
     fun getUserSettings(): LiveData<UserSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userSettings: UserSettings)

    @Update
    suspend fun update(userSettings: UserSettings)
}