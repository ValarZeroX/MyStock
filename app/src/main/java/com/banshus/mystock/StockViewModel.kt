package com.banshus.mystock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class StockViewModel : ViewModel() {
    var selectedTab by mutableIntStateOf(0)
    var selectedSettingScreen by mutableIntStateOf(0)
    var selectedTheme by mutableIntStateOf(0)
}