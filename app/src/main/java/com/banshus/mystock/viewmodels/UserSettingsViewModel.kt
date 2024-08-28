package com.banshus.mystock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banshus.mystock.data.dao.UserSettingsDao
import com.banshus.mystock.data.entities.UserSettings
import com.banshus.mystock.repository.UserSettingsRepository
import kotlinx.coroutines.launch

class UserSettingsViewModel(private val repository: UserSettingsRepository) : ViewModel() {
    val userSettings: LiveData<UserSettings> = repository.getUserSettings()

    fun updateUserSettings(newThemeIndex: Int) {
        viewModelScope.launch {
            // 获取当前的设置
            val currentSettings = userSettings.value
            // 更新数据库
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(themeIndex = newThemeIndex)
                repository.updateUserSettings(updatedSettings)
            }
        }
    }

    fun updateCurrencyCode(newCurrencyCode: String) {
        viewModelScope.launch {
            // 获取当前的设置
            val currentSettings = userSettings.value
            // 更新数据库
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(currency = newCurrencyCode)
                repository.updateUserSettings(updatedSettings)
            }
        }
    }


    fun updateReportSettings(isCommissionCalculationEnabled: Boolean,isTransactionTaxCalculationEnabled: Boolean, isDividendCalculationEnabled: Boolean) {
        viewModelScope.launch {
            // 获取当前的设置
            val currentSettings = userSettings.value
            // 更新数据库
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(
                    isCommissionCalculationEnabled = isCommissionCalculationEnabled,
                    isTransactionTaxCalculationEnabled = isTransactionTaxCalculationEnabled,
                    isDividendCalculationEnabled = isDividendCalculationEnabled
                    )
                repository.updateUserSettings(updatedSettings)
            }
        }
    }
}

class UserSettingsViewModelFactory(
    private val repository: UserSettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}