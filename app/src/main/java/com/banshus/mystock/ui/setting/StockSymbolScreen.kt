package com.banshus.mystock.ui.setting

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.api.RetrofitInstance
import com.banshus.mystock.api.response.StockChartResponse
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.repository.StockMarketRepository
import com.banshus.mystock.repository.StockPriceApiRepository
import com.banshus.mystock.repository.StockSymbolRepository
import com.banshus.mystock.viewmodels.StockMarketViewModel
import com.banshus.mystock.viewmodels.StockMarketViewModelFactory
import com.banshus.mystock.viewmodels.StockPriceApiViewModel
import com.banshus.mystock.viewmodels.StockPriceApiViewModelFactory
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModelFactory
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun StockSymbolScreen(navController: NavHostController) {
    // 创建 ScaffoldState
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    val decimalFormat = DecimalFormat("#.00")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val context = LocalContext.current
    val stockSymbolViewModel: StockSymbolViewModel = viewModel(
        factory = StockSymbolViewModelFactory(
            StockSymbolRepository(AppDatabase.getDatabase(context).stockSymbolDao())
        )
    )

    val stockMarketViewModel: StockMarketViewModel = viewModel(
        factory = StockMarketViewModelFactory(
            StockMarketRepository(AppDatabase.getDatabase(context).stockMarketDao())
        )
    )

    //使用api 初始化viewModel
    val stockPriceApiViewModel: StockPriceApiViewModel = viewModel(
        factory = StockPriceApiViewModelFactory(
            StockPriceApiRepository(RetrofitInstance.yahooApi)
        )
    )
    var stockChartResponses by remember { mutableStateOf<Map<String, StockChartResponse>>(emptyMap()) }

    var stockChartResponse by remember { mutableStateOf<StockChartResponse?>(null) }
    val stockPrice by stockPriceApiViewModel.stockPrice.observeAsState()
    LaunchedEffect(stockPrice) {
        stockChartResponse = stockPrice
    }

    val stockMarketList by stockMarketViewModel.allStockMarkets.observeAsState(emptyList())
    var selectedStockMarket by remember { mutableStateOf<StockMarket?>(null) }

    // Initialize search query and search active state
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedStockSymbol by remember { mutableStateOf<StockSymbol?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    val stockSymbolList by stockSymbolViewModel.stockSymbolsListByMarket.observeAsState(emptyList())

    //預設股票市場選擇第一筆
    LaunchedEffect(stockMarketList) {
        selectedStockMarket = if (stockMarketList.isNotEmpty()) {
            stockMarketList.first()
        } else {
            null
        }
    }

    LaunchedEffect(selectedStockMarket?.stockMarket) {
        selectedStockMarket?.let {
            stockSymbolViewModel.fetchStockSymbolsListByMarket(it.stockMarket)
        }
    }
    // Filtered stock symbols based on search query
    val filteredStockSymbolList = stockSymbolList.filter {
        it.stockSymbol.contains(searchQuery, ignoreCase = true) ||
                it.stockName.contains(searchQuery, ignoreCase = true)
    }

    stockPriceApiViewModel.stockPrice.observeAsState().value?.let { response ->
        val result = response.chart.result ?: return@let
        val symbol = result.firstOrNull()?.meta?.symbol ?: return@let
        stockChartResponses = stockChartResponses.toMutableMap().apply {
            put(symbol, response)
        }
    }

    Scaffold(
        topBar = {
            StockSymbolScreenHeader(
                navController,
//                onAddClick = { showAddDialog = true }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            StockMarketDropdown(
                stockMarkets = stockMarketList,
                selectedStockMarket = selectedStockMarket,
                onStockMarketSelected = { market ->
                    selectedStockMarket = market
                },
                onAddClick = { showAddDialog = true }

            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue -> searchQuery = newValue },
                label = { Text("搜索股票") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(filteredStockSymbolList) { stockSymbol ->
                    ListItem(
                        headlineContent = { Text("${stockSymbol.stockName} (${stockSymbol.stockSymbol})") },
                        supportingContent = {
                            Column {
                                Row{
                                val combinedSymbol =
                                    "${stockSymbol.stockSymbol}.${selectedStockMarket?.stockMarketCode}"
                                val response = stockChartResponses[combinedSymbol]
                                val price = response?.chart?.result?.firstOrNull()?.indicators?.quote?.firstOrNull()?.close?.lastOrNull()
                                    Text(
                                        text = price?.let { "股價: ${"%.2f".format(it)}" } ?: "股價: ${stockSymbol.stockPrice}",
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp), // Optional padding between rows
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    val lastUpdatedTimeFormatted = stockSymbol.lastUpdatedTime?.let {
                                        dateFormat.format(Date(it))
                                    } ?: "未知时间"
                                    Text(
                                        text = "最後更新時間: $lastUpdatedTimeFormatted", // Align text to the right
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        },
                        leadingContent = {
//                            Icon(
//                                Icons.Filled.Cached,
//                                contentDescription = "Localized description",
//                                modifier = Modifier.clickable {
//                                    stockPriceApiViewModel.fetchStockPriceResult(
//                                        symbol = stockSymbol.stockSymbol,
//                                        period1 = (System.currentTimeMillis() - 1000) / 1000,
//                                        period2 = System.currentTimeMillis() / 1000,
//                                        marketCode = selectedStockMarket?.stockMarketCode ?: ""
//                                    ){ response ->
//                                        val price = response.chart.result?.firstOrNull()?.indicators?.quote?.firstOrNull()?.close?.lastOrNull()
//                                        val roundedPrice = price?.let { decimalFormat.format(it).toDouble() }
//                                        val currentTime = System.currentTimeMillis()
//                                        stockSymbolViewModel.insertStockSymbol(
//                                            StockSymbol(
//                                                stockSymbol = stockSymbol.stockSymbol,
//                                                stockName = stockSymbol.stockName,
//                                                stockMarket = stockSymbol.stockMarket,
//                                                stockPrice = roundedPrice,
//                                                lastUpdatedTime = currentTime
//                                            )
//                                        )
//                                        showAddDialog = false
//                                    }
//                                }
//                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.clickable {
                                    selectedStockSymbol = stockSymbol
                                    showUpdateDialog = true
                                }
                            )
                        },
                    )
                    HorizontalDivider()
                }
            }
        }
        if (showAddDialog) {
            StockSymbolAdd(
                selectedStockMarket = selectedStockMarket,
                onDismiss = { showAddDialog = false },
                onAdd = { symbol, name, selectedMarketId ->
                    Log.d("StockSymbolAdd","No")
                    if (symbol.isNotEmpty()) {
                        Log.d("StockSymbolAdd","Go")
                        stockPriceApiViewModel.fetchStockPriceResult(
                            symbol = symbol,
                            period1 = System.currentTimeMillis() / 1000,
                            period2 = System.currentTimeMillis() / 1000,
                            marketCode = selectedStockMarket?.stockMarketCode ?: "",
                            onSuccess = { response ->
                                Log.d("StockChartResponse", "Meta Short Name: ${response?.chart?.result?.firstOrNull()?.meta?.shortName}")
                                val result = response?.chart?.result?.firstOrNull()
                                val meta = result?.meta

                                val regularMarketPrice = meta?.regularMarketPrice
                                val regularMarketDayHigh = meta?.regularMarketDayHigh
                                val regularMarketDayLow = meta?.regularMarketDayLow
//                                val shortName = meta?.shortName
                                val chartPreviousClose = meta?.chartPreviousClose
//                                Log.d("StockSymbolAdd Name","$shortName")
                                val roundedPrice = regularMarketPrice?.let { decimalFormat.format(it).toDouble() }
                                val currentTime = System.currentTimeMillis()
                                stockSymbolViewModel.insertStockSymbol(
                                    StockSymbol(
                                        stockSymbol = symbol,
                                        stockName = name,
                                        stockMarket = selectedMarketId,
                                        stockPrice = roundedPrice,
                                        regularMarketDayLow = regularMarketDayLow,
                                        regularMarketDayHigh = regularMarketDayHigh,
                                        chartPreviousClose =chartPreviousClose,
                                        lastUpdatedTime = currentTime
                                    )
                                )
                                scope.launch {
                                    snackbarHostState.showSnackbar("新增成功")
                                }
                            },
                            onError = { errorMessage ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("$errorMessage")
                                }
//                                showAddDialog = false
                            }
                        )
                        showAddDialog = false
                    }
                }
//                onAdd = { symbol, name, selectedMarketId ->
//                    if (symbol.isNotEmpty() && name.isNotEmpty()) {
//                        stockSymbolViewModel.insertStockSymbol(
//                            StockSymbol(
//                                stockSymbol = symbol,
//                                stockName = name,
//                                stockMarket = selectedMarketId
//                            )
//                        )
//                        showAddDialog = false
//                    }
//                }
            )
        }
        if (showUpdateDialog) {
            StockSymbolUpdate(
                stockSymbol = selectedStockSymbol!!.stockSymbol,
                stockMarket = selectedStockSymbol!!.stockMarket,
                onDismiss = { showUpdateDialog = false },
                onUpdate = { symbol, nameUpdate, marketId ->
                    if (nameUpdate.isNotEmpty()) {
                        stockSymbolViewModel.updateStockName(symbol, marketId, nameUpdate)
                        showUpdateDialog = false
                    }
                }
            )
        }
    }
}
@Composable
fun StockSymbolUpdate(
    stockSymbol: String,
    stockMarket: Int,
    onDismiss: () -> Unit,
    onUpdate: (String, String, Int) -> Unit
) {
    var nameUpdate by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "更新股票",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider()

                // 顯示股票代碼
                Text(
                    text = "股票代碼: $stockSymbol",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontSize = 16.sp
                )

                OutlinedTextField(
                    value = nameUpdate,
                    onValueChange = { newName ->
                        if (newName.length <= 40) {
                            nameUpdate = newName
                        }
                    },
                    label = { Text("股票名稱") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onUpdate(stockSymbol, nameUpdate, stockMarket)
                        }
                    ) {
                        Text("更新")
                    }
                }
            }
        }
    }
}

@Composable
fun StockSymbolAdd(
    selectedStockMarket: StockMarket?,
    onDismiss: () -> Unit,
    onAdd: (String, String, Int) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "新增股票",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider()
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { newName ->
                        if (newName.length <= 40) {
                            symbol = newName
                        }
                    },
                    label = { Text("股票代碼") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { newName ->
                        if (newName.length <= 40) {
                            name = newName
                        }
                    },
                    label = { Text("股票名稱") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val marketId = selectedStockMarket?.stockMarket ?: -1
                            onAdd(symbol, name, marketId)
                        }
                    ) {
                        Text("新增")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSymbolScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "股票代碼",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "返回"
                )
            }
        },
//        actions = {
//            IconButton(onClick = onAddClick) {
//                Icon(
//                    imageVector = Icons.Filled.Add,
//                    contentDescription = "新增"
//                )
//            }
//        }
    )
}

@Composable
fun StockMarketDropdown(
    stockMarkets: List<StockMarket>, // 替換為你的股票市場數據類型
    selectedStockMarket: StockMarket?,
    onStockMarketSelected: (StockMarket) -> Unit,
    onAddClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = selectedStockMarket?.stockMarketName ?: "", // 假設 stockMarketName 是股票市場的顯示名稱
            onValueChange = {},
            label = { Text("選擇股票市場") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.clickable {
                        onAddClick()
                    }
                )
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = true
                            }
                        }
                    }
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("搜尋股票市場") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            )
            val filteredStockMarkets = stockMarkets.filter {
                it.stockMarketName.contains(
                    searchQuery,
                    ignoreCase = true
                ) // 假設 stockMarketName 是股票市場的顯示名稱
            }
            filteredStockMarkets.forEach { stockMarket ->
                DropdownMenuItem(
                    text = { Text(stockMarket.stockMarketName) }, // 顯示股票市場名稱
                    onClick = {
                        onStockMarketSelected(stockMarket)
                        expanded = false
                    }
                )
            }
        }
    }
}