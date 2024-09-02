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

    fun updateAutoExchangeRate(autoUpdateExchangeRate: Boolean) {
        viewModelScope.launch {
            val currentSettings = userSettings.value
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(autoUpdateExchangeRate = autoUpdateExchangeRate)
                repository.updateUserSettings(updatedSettings)
            }
        }
    }

    fun updateAutoStockPrice(autoUpdateStock: Boolean) {
        viewModelScope.launch {
            val currentSettings = userSettings.value
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(autoUpdateStock = autoUpdateStock)
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

    fun updateDarkTheme(darkTheme: Boolean) {
        viewModelScope.launch {
            val currentSettings = userSettings.value
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(darkTheme = darkTheme)
                repository.updateUserSettings(updatedSettings)
            }
        }
    }

    // 获取自动更新股价的设置和间隔
    fun shouldAutoUpdateStockPrice(): Boolean {
        return userSettings.value?.autoUpdateStock ?: false
    }

    fun getStockUpdateInterval(): Int {
        return userSettings.value?.autoUpdateStockSecond ?: 180
    }

    // 更新自动更新股价的时间间隔
    fun updateAutoStockPriceInterval(seconds: Int) {
        viewModelScope.launch {
            val currentSettings = userSettings.value
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(autoUpdateStockSecond = seconds)
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