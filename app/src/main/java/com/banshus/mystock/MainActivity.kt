package com.banshus.mystock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.UserSettingsRepository
import com.banshus.mystock.ui.NavBar
import com.banshus.mystock.ui.StockAddScreen
import com.banshus.mystock.ui.StockReportScreen
import com.banshus.mystock.ui.MySetting
import com.banshus.mystock.ui.theme.MyStockTheme
import com.banshus.mystock.viewmodels.UserSettingsViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModelFactory


class MainActivity : ComponentActivity() {
//    private val userSettingsViewModel by viewModels<UserSettingsViewModel>()
//    private lateinit var userSettingsViewModel: UserSettingsViewModel
    private val viewModel by viewModels<StockViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = AppDatabase.getDatabase(this).userSettingsDao()
        val repository = UserSettingsRepository(dao)
        val factory = UserSettingsViewModelFactory(repository)
//        userSettingsViewModel = ViewModelProvider(this, factory).get(UserSettingsViewModel::class.java)
        setContent {
//            println(userSettingsViewModel.userSettings)
            MyStockTheme(
                darkTheme = true,
                dynamicColor = false,
                themeIndex = 1
            ) {
                Surface(tonalElevation = 5.dp) {
                    Box {
                        Column(modifier = Modifier.systemBarsPadding()){
                            when (viewModel.selectedTab) {
                                0 -> StockAddScreen()
                                1 -> StockAddScreen()
                                2 -> StockAddScreen()
                                3 -> StockReportScreen(modifier = Modifier.weight(1f))
                                4 -> MySetting()
                            }
                        }
//                        userSettingsViewModel.loadUserSettings()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter) // Align NavBar to the bottom
                                .background(Color.Transparent)
                        ) {
                            NavBar(
                                viewModel.selectedTab,
                                onTabSelected = { index ->
                                    viewModel.selectedTab = index
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
