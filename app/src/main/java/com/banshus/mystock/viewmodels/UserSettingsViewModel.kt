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
    private val _userSettings: MutableLiveData<UserSettings> = MutableLiveData()
    val userSettings: LiveData<UserSettings> get() = _userSettings

    fun loadUserSettings() {
        viewModelScope.launch {
            repository.getUserSettings().observeForever {
                _userSettings.postValue(it)
            }
        }
    }
}
//class UserSettingsViewModel(private val repository: UserSettingsRepository) : ViewModel() {
//    private val _userSettings = MutableLiveData<UserSettings?>()
//    val userSettings: LiveData<UserSettings?> get() = _userSettings
//
//    init {
//        // 在初始化时加载数据
//        viewModelScope.launch {
//            _userSettings.value = repository.getUserSettings()
//        }
//    }
//    fun insert(userSettings: UserSettings) = viewModelScope.launch {
//        repository.insert(userSettings)
//        _userSettings.value = repository.getUserSettings()
//    }
//
//    fun update(userSettings: UserSettings) = viewModelScope.launch {
//        repository.update(userSettings)
//        _userSettings.value = repository.getUserSettings()
//    }
//}

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