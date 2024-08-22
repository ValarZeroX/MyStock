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
import com.banshus.mystock.api.RetrofitInstance
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.StockAccountRepository
import com.banshus.mystock.repository.StockMarketRepository
import com.banshus.mystock.repository.StockPriceApiRepository
import com.banshus.mystock.repository.StockRecordRepository
import com.banshus.mystock.repository.StockSymbolRepository
import com.banshus.mystock.repository.UserSettingsRepository
import com.banshus.mystock.ui.NavBar
import com.banshus.mystock.ui.StockSettingScreen
import com.banshus.mystock.ui.record.RecordScreen
import com.banshus.mystock.ui.report.ReportScreen
import com.banshus.mystock.ui.setting.AccountScreen
import com.banshus.mystock.ui.setting.ColorThemeScreen
import com.banshus.mystock.ui.setting.EditAccountScreen
import com.banshus.mystock.ui.setting.StockMarketScreen
import com.banshus.mystock.ui.setting.StockSymbolScreen
import com.banshus.mystock.ui.stock.AccountListScreen
import com.banshus.mystock.ui.stock.AddAccountScreen
import com.banshus.mystock.ui.stock.AddStockScreen
import com.banshus.mystock.ui.stock.StockAccountScreen
import com.banshus.mystock.ui.stock.StockDetailScreen
import com.banshus.mystock.ui.stock.StockListScreen
import com.banshus.mystock.ui.theme.MyStockTheme
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModelFactory
import com.banshus.mystock.viewmodels.StockMarketViewModel
import com.banshus.mystock.viewmodels.StockMarketViewModelFactory
import com.banshus.mystock.viewmodels.StockPriceApiViewModel
import com.banshus.mystock.viewmodels.StockPriceApiViewModelFactory
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModelFactory
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModelFactory
import com.banshus.mystock.viewmodels.UserSettingsViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModelFactory
import com.github.mikephil.charting.utils.Utils


class MainActivity : ComponentActivity() {
//    private val userSettingsViewModel by viewModels<UserSettingsViewModel>()
    private lateinit var userSettingsViewModel: UserSettingsViewModel
    private lateinit var stockAccountViewModel: StockAccountViewModel
    private lateinit var stockRecordViewModel: StockRecordViewModel
    private lateinit var stockSymbolViewModel: StockSymbolViewModel
    private lateinit var stockMarketViewModel: StockMarketViewModel
    private lateinit var stockPriceApiViewModel: StockPriceApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        //圖表初始化
        Utils.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = AppDatabase.getDatabase(this).userSettingsDao()
        val repository = UserSettingsRepository(dao)
        val factory = UserSettingsViewModelFactory(repository)
        userSettingsViewModel = ViewModelProvider(this, factory)[UserSettingsViewModel::class.java]

        // 数据库和 Repository 的初始化
        val database = AppDatabase.getDatabase(this)
        val stockAccountRepository = StockAccountRepository(database.stockAccountDao())
        val stockRecordRepository = StockRecordRepository(database.stockRecordDao())
        val stockSymbolRepository = StockSymbolRepository(database.stockSymbolDao())
        val stockMarketRepository = StockMarketRepository(database.stockMarketDao())
        val stockPriceApiRepository = StockPriceApiRepository(RetrofitInstance.yahooApi)

        // ViewModel 的初始化
        stockAccountViewModel = ViewModelProvider(this, StockAccountViewModelFactory(stockAccountRepository))[StockAccountViewModel::class.java]
        stockRecordViewModel = ViewModelProvider(this, StockRecordViewModelFactory(stockRecordRepository))[StockRecordViewModel::class.java]
        stockSymbolViewModel = ViewModelProvider(this, StockSymbolViewModelFactory(stockSymbolRepository))[StockSymbolViewModel::class.java]
        stockMarketViewModel = ViewModelProvider(this, StockMarketViewModelFactory(stockMarketRepository))[StockMarketViewModel::class.java]
        stockPriceApiViewModel = ViewModelProvider(this, StockPriceApiViewModelFactory(stockPriceApiRepository))[StockPriceApiViewModel::class.java]


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
                            MyApp(
                                navController,
                                stockAccountViewModel = stockAccountViewModel,
                                stockRecordViewModel = stockRecordViewModel,
                                stockSymbolViewModel = stockSymbolViewModel,
                                stockMarketViewModel = stockMarketViewModel,
                                stockPriceApiViewModel = stockPriceApiViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(
    navController: NavHostController,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel,
    stockMarketViewModel: StockMarketViewModel,
    stockPriceApiViewModel: StockPriceApiViewModel
) {
//    val viewModel: StockViewModel = viewModel()
    val stockViewModel: StockViewModel = viewModel()
    NavHost(navController = navController, startDestination = "StockAccountScreen") {
        composable("stockAccountScreen") {
            StockAccountScreen(navController, stockViewModel, stockAccountViewModel, stockRecordViewModel, stockSymbolViewModel)
        }
        composable("addAccountScreen") {
            AddAccountScreen(navController, stockAccountViewModel)
        }
        composable("accountListScreen") {
            AccountListScreen(navController, stockViewModel, stockAccountViewModel)
        }
        composable("stockSettingScreen") {
            StockSettingScreen(navController)
        }
        composable("colorThemeScreen") {
            ColorThemeScreen(navController)
        }
        composable("addStockScreen") {
            AddStockScreen(navController, stockViewModel,stockAccountViewModel, stockRecordViewModel, stockSymbolViewModel)
        }
        composable("stockMarketScreen") {
            StockMarketScreen(navController,stockMarketViewModel)
        }
        composable("stockSymbolScreen") {
            StockSymbolScreen(navController,stockSymbolViewModel,stockMarketViewModel, stockPriceApiViewModel )
        }
        composable("stockListScreen") {
            StockListScreen(navController, stockViewModel,stockAccountViewModel, stockRecordViewModel, stockSymbolViewModel)
        }
        composable("stockDetailScreen") {
            StockDetailScreen(navController, stockViewModel,stockAccountViewModel, stockRecordViewModel, stockSymbolViewModel)
        }
        composable("reportScreen") {
            ReportScreen(navController, stockViewModel,stockAccountViewModel, stockRecordViewModel, stockSymbolViewModel)
        }
        composable("accountScreen") {
            AccountScreen(navController, stockViewModel, stockAccountViewModel, stockRecordViewModel)
        }
        composable("editAccountScreen") {
            EditAccountScreen(navController, stockViewModel, stockAccountViewModel)
        }
        composable("recordScreen") {
            RecordScreen(navController, stockViewModel, stockAccountViewModel,stockSymbolViewModel,  stockRecordViewModel )
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