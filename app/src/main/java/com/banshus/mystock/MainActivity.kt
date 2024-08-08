package com.banshus.mystock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.UserSettingsRepository
import com.banshus.mystock.ui.NavBar
import com.banshus.mystock.ui.stock.StockAddScreen
import com.banshus.mystock.ui.StockSettingScreen
import com.banshus.mystock.ui.setting.ColorThemeScreen
import com.banshus.mystock.ui.setting.StockMarketScreen
import com.banshus.mystock.ui.setting.StockSymbolScreen
import com.banshus.mystock.ui.stock.AccountHeader
import com.banshus.mystock.ui.stock.AccountListScreen
import com.banshus.mystock.ui.stock.AddAccountScreen
import com.banshus.mystock.ui.stock.StockAccountScreen
import com.banshus.mystock.ui.stock.StockListScreen
import com.banshus.mystock.ui.theme.MyStockTheme
import com.banshus.mystock.viewmodels.UserSettingsViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModelFactory


class MainActivity : ComponentActivity() {
//    private val userSettingsViewModel by viewModels<UserSettingsViewModel>()
    private lateinit var userSettingsViewModel: UserSettingsViewModel


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
                    Scaffold(
                        bottomBar = {
                            NavBar(
                                selectedItemIndex,
                                navController
                            )
                        },
                        topBar = {}
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(PaddingValues(bottom = innerPadding.calculateBottomPadding()))) {
                            MyApp(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(navController: NavHostController) {
//    val viewModel: StockViewModel = viewModel()
    val stockViewModel: StockViewModel = viewModel()
    NavHost(navController = navController, startDestination = "StockAccountScreen") {
        composable("stockAccountScreen") {
            StockAccountScreen(navController, stockViewModel)
        }
        composable("addAccountScreen") {
            AddAccountScreen(navController)
        }
        composable("accountListScreen") {
            AccountListScreen(navController, stockViewModel)
        }
        composable("stockSettingScreen") {
            StockSettingScreen(navController)
        }
        composable("colorThemeScreen") {
            ColorThemeScreen(navController)
        }
        composable("stockAddScreen") {
            StockAddScreen(navController, stockViewModel)
        }
        composable("stockMarketScreen") {
            StockMarketScreen(navController)
        }
        composable("stockSymbolScreen") {
            StockSymbolScreen(navController)
        }
        composable("stockListScreen") {
            StockListScreen(navController, stockViewModel)
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