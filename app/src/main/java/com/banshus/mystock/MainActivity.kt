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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.UserSettingsRepository
import com.banshus.mystock.ui.NavBar
import com.banshus.mystock.ui.StockAddScreen
import com.banshus.mystock.ui.StockReportScreen
import com.banshus.mystock.ui.StockSettingScreen
import com.banshus.mystock.ui.setting.ColorThemeScreen
import com.banshus.mystock.ui.stock.AccountListScreen
import com.banshus.mystock.ui.stock.AddAccountScreen
import com.banshus.mystock.ui.stock.StockAccountScreen
import com.banshus.mystock.ui.theme.MyStockTheme
import com.banshus.mystock.viewmodels.UserSettingsViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModelFactory


class MainActivity : ComponentActivity() {
//    private val userSettingsViewModel by viewModels<UserSettingsViewModel>()
    private lateinit var userSettingsViewModel: UserSettingsViewModel
//    private val viewModel by viewModels<StockViewModel>()


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
            MyStockTheme(
                userSettingsViewModel = userSettingsViewModel,
                darkTheme = true,
                dynamicColor = false
            ) {
                Surface(tonalElevation = 5.dp) {
                    val navController = rememberNavController()
                    val selectedItemIndex = rememberSaveable { mutableIntStateOf(0) }
//                    when (selectedItemIndex.intValue) {
//                        0 -> navController.navigate("stockAccountScreen")
//                        1 -> navController.navigate("stockAccountScreen")
//                        2 -> navController.navigate("stockAccountScreen")
//                        3 -> navController.navigate("stockAccountScreen")
//                        4 -> navController.navigate("stockSettingScreen")
//                    }

                    Scaffold(
                        bottomBar = {
                            NavBar(
                                selectedItemIndex,
                                navController
                            )
                        }
                    ) { innerPadding ->
                        MyApp(navController, innerPadding)
                    }
//                    Box {
//                        Column(modifier = Modifier.systemBarsPadding()){
//                            when (viewModel.selectedTab) {
//                                0 -> MyApp()
//                                1 -> StockAddScreen()
//                                2 -> StockAddScreen()
//                                3 -> StockReportScreen(modifier = Modifier.weight(1f))
//                                4 -> MyApp()
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .align(Alignment.BottomCenter) // Align NavBar to the bottom
//                                .background(Color.Transparent)
//                        ) {
//                            NavBar(
//                                viewModel.selectedTab,
//                                onTabSelected = { index ->
//                                    viewModel.selectedTab = index
//                                }
//                            )
//                        }
//                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(navController = navController, startDestination = "StockAccountScreen") {
        composable("stockAccountScreen") {
            StockAccountScreen(navController)
        }
        composable("addAccountScreen") {
            AddAccountScreen(navController)
        }
        composable("accountListScreen") {
            AccountListScreen(navController)
        }
        composable("stockSettingScreen") {
            StockSettingScreen(navController)
        }
        composable("colorThemeScreen") {
            ColorThemeScreen(navController)
        }
        composable("stockAddScreen") {
            StockAddScreen(navController)
        }
    }
}

//@Composable
//fun MySetting() {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = "stockSettingScreen") {
//        composable("stockSettingScreen") {
//            StockSettingScreen(navController)
//        }
//        composable("colorThemeScreen") {
//            ColorThemeScreen(navController)
//        }
//    }
//}