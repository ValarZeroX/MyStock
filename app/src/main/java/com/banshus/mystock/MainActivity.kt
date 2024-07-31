package com.banshus.mystock

import android.os.Bundle
import android.widget.TextView
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.UserSettingsRepository
import com.banshus.mystock.ui.MyAccount
import com.banshus.mystock.ui.NavBar
import com.banshus.mystock.ui.StockAddScreen
import com.banshus.mystock.ui.StockReportScreen
import com.banshus.mystock.ui.MySetting
import com.banshus.mystock.ui.theme.MyStockTheme
import com.banshus.mystock.viewmodels.UserSettingsViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModelFactory


class MainActivity : ComponentActivity() {
//    private val userSettingsViewModel by viewModels<UserSettingsViewModel>()
    private lateinit var userSettingsViewModel: UserSettingsViewModel
    private val viewModel by viewModels<StockViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = AppDatabase.getDatabase(this).userSettingsDao()
        val repository = UserSettingsRepository(dao)
        val factory = UserSettingsViewModelFactory(repository)
        userSettingsViewModel = ViewModelProvider(this, factory)[UserSettingsViewModel::class.java]

//        userSettingsViewModel = ViewModelProvider(
//            this,
//            UserSettingsViewModelFactory(repository)
//        )[UserSettingsViewModel::class.java]
        setContent {
            println("hello")
//            val userSettings = userSettingsViewModel.userSettings.observeAsState(emptyList())
//
//            println(userSettings)

//            userSettings.value?.let {
//                Text(text = "当前主题索引: ${it.themeIndex}")
//            }
//            println(userSettingsViewModel.userSettings)
            println("hello")
            MyStockTheme(
                userSettingsViewModel = userSettingsViewModel,
                darkTheme = true,
                dynamicColor = false
            ) {
                Surface(tonalElevation = 5.dp) {
                    Box {
                        Column(modifier = Modifier.systemBarsPadding()){
                            when (viewModel.selectedTab) {
                                0 -> MyAccount()
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
