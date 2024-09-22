package com.banshus.mystock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.banshus.mystock.api.RetrofitInstance
import com.banshus.mystock.billing.BillingManager
import com.banshus.mystock.csv.importDataFromCSV
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.CurrencyApiRepository
import com.banshus.mystock.repository.CurrencyRepository
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
import com.banshus.mystock.ui.setting.AutoUpdateScreen
import com.banshus.mystock.ui.setting.BillingScreen
import com.banshus.mystock.ui.setting.ColorThemeScreen
import com.banshus.mystock.ui.setting.CurrencyScreen
import com.banshus.mystock.ui.setting.DisclaimerScreen
import com.banshus.mystock.ui.setting.EditAccountScreen
import com.banshus.mystock.ui.setting.ReportSettingScreen
import com.banshus.mystock.ui.setting.StockMarketScreen
import com.banshus.mystock.ui.setting.StockSymbolScreen
import com.banshus.mystock.ui.stock.AccountListScreen
import com.banshus.mystock.ui.stock.AddAccountScreen
import com.banshus.mystock.ui.stock.AddStockScreen
import com.banshus.mystock.ui.stock.MarketListScreen
import com.banshus.mystock.ui.stock.StockAccountScreen
import com.banshus.mystock.ui.stock.StockDetailScreen
import com.banshus.mystock.ui.stock.StockListScreen
import com.banshus.mystock.ui.theme.MyStockTheme
import com.banshus.mystock.viewmodels.BillingViewModel
import com.banshus.mystock.viewmodels.BillingViewModelFactory
import com.banshus.mystock.viewmodels.CurrencyApiViewModel
import com.banshus.mystock.viewmodels.CurrencyApiViewModelFactory
import com.banshus.mystock.viewmodels.CurrencyViewModel
import com.banshus.mystock.viewmodels.CurrencyViewModelFactory
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
import com.banshus.mystock.work.CurrencyRateUpdateWorker
import com.banshus.mystock.work.StockPriceUpdateWorker
import com.github.mikephil.charting.utils.Utils
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var userSettingsViewModel: UserSettingsViewModel
    private lateinit var stockAccountViewModel: StockAccountViewModel
    private lateinit var stockRecordViewModel: StockRecordViewModel
    private lateinit var stockSymbolViewModel: StockSymbolViewModel
    private lateinit var stockMarketViewModel: StockMarketViewModel
    private lateinit var stockPriceApiViewModel: StockPriceApiViewModel
    private lateinit var currencyViewModel: CurrencyViewModel
    private lateinit var currencyApiViewModel: CurrencyApiViewModel
    private lateinit var csvImportLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        //圖表初始化
        Utils.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // google廣告
        MobileAds.initialize(this) {}
//        val backgroundScope = CoroutineScope(Dispatchers.IO)
//        backgroundScope.launch {
//            // Initialize the Google Mobile Ads SDK on a background thread.
//            MobileAds.initialize(this@MainActivity) {}
//        }
        // 注册 ActivityResultLauncher
        csvImportLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    lifecycleScope.launch {
                        importDataFromCSV(applicationContext, uri)
                        // 在这里调用你的CSV导入函数，比如 importStockRecordsFromCSV(applicationContext, uri)
                    }
                }
            }
        }

        val billingManager = BillingManager(this)
        val billingViewModel: BillingViewModel = ViewModelProvider(
            this,
            BillingViewModelFactory(billingManager)
        )[BillingViewModel::class.java]
        billingViewModel.startBillingConnection()
        billingViewModel.hasSubscription()


        // 数据库和 Repository 的初始化
        val database = AppDatabase.getDatabase(this)
        val stockAccountRepository = StockAccountRepository(database.stockAccountDao())
        val stockRecordRepository = StockRecordRepository(database.stockRecordDao())
        val stockSymbolRepository = StockSymbolRepository(database.stockSymbolDao())
        val stockMarketRepository = StockMarketRepository(database.stockMarketDao())
        val stockPriceApiRepository = StockPriceApiRepository(RetrofitInstance.yahooApi)
        val userSettingsRepository = UserSettingsRepository(database.userSettingsDao())
        val currencyRepository = CurrencyRepository(database.currencyDao())
        val currencyApiRepository = CurrencyApiRepository(RetrofitInstance.currencyApi)


        // ViewModel 的初始化
        stockAccountViewModel = ViewModelProvider(
            this,
            StockAccountViewModelFactory(stockAccountRepository)
        )[StockAccountViewModel::class.java]
        stockRecordViewModel = ViewModelProvider(
            this,
            StockRecordViewModelFactory(stockRecordRepository)
        )[StockRecordViewModel::class.java]
        stockSymbolViewModel = ViewModelProvider(
            this,
            StockSymbolViewModelFactory(stockSymbolRepository)
        )[StockSymbolViewModel::class.java]
        stockMarketViewModel = ViewModelProvider(
            this,
            StockMarketViewModelFactory(stockMarketRepository)
        )[StockMarketViewModel::class.java]
        stockPriceApiViewModel = ViewModelProvider(
            this,
            StockPriceApiViewModelFactory(stockPriceApiRepository)
        )[StockPriceApiViewModel::class.java]
        userSettingsViewModel = ViewModelProvider(
            this,
            UserSettingsViewModelFactory(userSettingsRepository)
        )[UserSettingsViewModel::class.java]
        currencyViewModel = ViewModelProvider(
            this,
            CurrencyViewModelFactory(currencyRepository)
        )[CurrencyViewModel::class.java]
        currencyApiViewModel = ViewModelProvider(
            this,
            CurrencyApiViewModelFactory(currencyApiRepository)
        )[CurrencyApiViewModel::class.java]

        // 检查是否需要自动更新股价
        userSettingsViewModel.userSettings.observe(this) { settings ->
            settings?.let {
                if (settings.autoUpdateStock) {
                    // 最小更新间隔为 15 分钟
                    val intervalInMinutes = maxOf(15L, settings.autoUpdateStockSecond / 60L)
                    val stockPriceUpdateRequest =
                        PeriodicWorkRequestBuilder<StockPriceUpdateWorker>(
                            intervalInMinutes, TimeUnit.MINUTES
                        ).build()

                    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                        "StockPriceUpdate",
                        ExistingPeriodicWorkPolicy.UPDATE,  // 使用 REPLACE 策略来替换现有任务
                        stockPriceUpdateRequest
                    )
                } else {
                    WorkManager.getInstance(applicationContext).cancelUniqueWork("StockPriceUpdate")
                }

                if (settings.autoUpdateExchangeRate) {
                    val currencyIntervalInMinutes =
                        maxOf(15L, settings.autoUpdateExchangeRateSecond / 60L)
                    val currencyRateUpdateRequest =
                        PeriodicWorkRequestBuilder<CurrencyRateUpdateWorker>(
                            currencyIntervalInMinutes, TimeUnit.MINUTES
                        ).build()

                    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                        "CurrencyRateUpdate",
                        ExistingPeriodicWorkPolicy.UPDATE,  // 使用 REPLACE 策略来替换现有任务
                        currencyRateUpdateRequest
                    )
                } else {
                    WorkManager.getInstance(applicationContext)
                        .cancelUniqueWork("CurrencyRateUpdate")
                }
            }
        }

        lifecycleScope.launch {
            StockPriceUpdateWorker.updateStockPrices(applicationContext)
            CurrencyRateUpdateWorker.updateCurrencyRates(applicationContext)
        }



        setContent {
            MyStockTheme(
                userSettingsViewModel = userSettingsViewModel,
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
                                stockPriceApiViewModel = stockPriceApiViewModel,
                                userSettingsViewModel = userSettingsViewModel,
                                currencyViewModel = currencyViewModel,
                                currencyApiViewModel = currencyApiViewModel,
                                csvImportLauncher = csvImportLauncher,
                                billingViewModel  = billingViewModel
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
    stockPriceApiViewModel: StockPriceApiViewModel,
    userSettingsViewModel: UserSettingsViewModel,
    currencyViewModel: CurrencyViewModel,
    currencyApiViewModel: CurrencyApiViewModel,
    csvImportLauncher: ActivityResultLauncher<Intent>,
    billingViewModel : BillingViewModel
) {
//    val viewModel: StockViewModel = viewModel()
    val stockViewModel: StockViewModel = viewModel()
    NavHost(navController = navController, startDestination = "StockAccountScreen") {
        composable("stockAccountScreen") {
            StockAccountScreen(
                navController,
                stockViewModel,
                stockAccountViewModel,
                stockRecordViewModel,
                stockSymbolViewModel
            )
        }
        composable("addAccountScreen") {
            AddAccountScreen(navController, stockAccountViewModel, currencyViewModel, currencyApiViewModel)
        }
        composable("accountListScreen") {
            AccountListScreen(navController, stockViewModel, stockAccountViewModel)
        }
        composable("stockSettingScreen") {
            StockSettingScreen(navController, csvImportLauncher)
        }
        composable("colorThemeScreen") {
            ColorThemeScreen(navController, userSettingsViewModel)
        }
        composable("addStockScreen") {
            AddStockScreen(
                navController,
                stockViewModel,
                stockAccountViewModel,
                stockRecordViewModel,
                stockSymbolViewModel
            )
        }
        composable("stockMarketScreen") {
            StockMarketScreen(navController, stockMarketViewModel)
        }
        composable("stockSymbolScreen") {
            StockSymbolScreen(
                navController,
                stockSymbolViewModel,
                stockMarketViewModel,
                stockPriceApiViewModel
            )
        }
        composable("stockListScreen") {
            StockListScreen(
                navController,
                stockViewModel,
                stockAccountViewModel,
                stockRecordViewModel,
                stockSymbolViewModel
            )
        }
        composable("stockDetailScreen") {
            StockDetailScreen(
                navController,
                stockViewModel,
                stockAccountViewModel,
                stockRecordViewModel,
                stockSymbolViewModel
            )
        }
        composable("reportScreen") {
            ReportScreen(
                navController,
                stockViewModel,
                stockAccountViewModel,
                stockRecordViewModel,
                stockSymbolViewModel,
                userSettingsViewModel,
                stockMarketViewModel,
                currencyViewModel
            )
        }
        composable("accountScreen") {
            AccountScreen(
                navController,
                stockViewModel,
                stockAccountViewModel,
                stockRecordViewModel
            )
        }
        composable("editAccountScreen") {
            EditAccountScreen(navController, stockViewModel, stockAccountViewModel)
        }
        composable("recordScreen") {
            RecordScreen(
                navController,
                stockViewModel,
                stockAccountViewModel,
                stockSymbolViewModel,
                stockRecordViewModel
            )
        }
        composable("reportSettingScreen") {
            ReportSettingScreen(navController, userSettingsViewModel)
        }
        composable("currencyScreen") {
            CurrencyScreen(
                navController,
                userSettingsViewModel,
                currencyViewModel,
                currencyApiViewModel
            )
        }
        composable("autoUpdateScreen") {
            AutoUpdateScreen(
                navController,
                stockViewModel,
                userSettingsViewModel
            )
        }
        composable("disclaimerScreen") {
            DisclaimerScreen(
                navController,
            )
        }
        composable("marketListScreen") {
            MarketListScreen(
                navController,
                stockViewModel,
                stockMarketViewModel,
            )
        }
        composable("billingScreen") {
            BillingScreen(
                navController,
                billingViewModel
            )
        }
    }
}
