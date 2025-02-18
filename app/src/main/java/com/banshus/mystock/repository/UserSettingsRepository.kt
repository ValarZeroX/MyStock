package com.banshus.mystock.repository

import androidx.lifecycle.LiveData
import com.banshus.mystock.data.dao.UserSettingsDao
import com.banshus.mystock.data.entities.UserSettings

class UserSettingsRepository(private val userSettingsDao: UserSettingsDao) {
    fun getUserSettings(): LiveData<UserSettings> {
        return userSettingsDao.getUserSettings()
    }

    suspend fun updateUserSettings(userSettings: UserSettings) {
        userSettingsDao.update(userSettings)
    }
//    var allSetting: LiveData<List<UserSettings>> = userSettingsDao.getUserSettings()
//    val userSettings: UserSettings? = userSettingsDao.getUserSettings()
//
//    suspend fun getUserSettings(): UserSettings? {
//        return userSettingsDao.getUserSettings()
//    }
//
//    suspend fun insert(userSettings: UserSettings) {
//        userSettingsDao.insert(userSettings)
//    }
//
//    suspend fun update(userSettings: UserSettings) {
//        userSettingsDao.update(userSettings)
//    }
}