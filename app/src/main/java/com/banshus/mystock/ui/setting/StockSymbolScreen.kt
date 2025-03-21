package com.banshus.mystock.ui.setting

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.api.response.StockChartResponse
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.viewmodels.BillingViewModel
import com.banshus.mystock.viewmodels.StockMarketViewModel
import com.banshus.mystock.viewmodels.StockPriceApiViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun StockSymbolScreen(
    navController: NavHostController,
    stockSymbolViewModel: StockSymbolViewModel,
    stockMarketViewModel: StockMarketViewModel,
    stockPriceApiViewModel: StockPriceApiViewModel,
    billingViewModel: BillingViewModel
) {
    val context = LocalContext.current
    // 创建 ScaffoldState
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    val decimalFormat = DecimalFormat("#.00")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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

    var filteredStockSymbolList by remember { mutableStateOf(stockSymbolList) }

    // Filtered stock symbols based on search query
    LaunchedEffect(stockSymbolList) {
         filteredStockSymbolList = stockSymbolList.filter {
            it.stockSymbol.contains(searchQuery, ignoreCase = true) ||
                    it.stockName.contains(searchQuery, ignoreCase = true)
        }
    }
    stockPriceApiViewModel.stockPrice.observeAsState().value?.let { response ->
        val result = response.chart.result ?: return@let
        val symbol = result.firstOrNull()?.meta?.symbol ?: return@let
        stockChartResponses = stockChartResponses.toMutableMap().apply {
            put(symbol, response)
        }
    }
    val updateSuccessfulMessage = stringResource(id = R.string.update_successful)
    val addSuccessfulMessage = stringResource(id = R.string.addition_successful)
    Scaffold(
        topBar = {
            StockSymbolScreenHeader(
                navController,
//                onAddClick = { showAddDialog = true }
            )
        },
        bottomBar = {
            AdBanner(billingViewModel) // 将广告放在底部栏
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
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue -> searchQuery = newValue },
                label = { Text(stringResource(id = R.string.search_stock)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),

                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Stock Symbol",
                        modifier = Modifier.clickable {
                            showAddDialog = true
                        }
                    )
                },
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
                                Row {
                                    val combinedSymbol =
                                        "${stockSymbol.stockSymbol}.${selectedStockMarket?.stockMarketCode}"
                                    val response = stockChartResponses[combinedSymbol]
                                    val price =
                                        response?.chart?.result?.firstOrNull()?.indicators?.quote?.firstOrNull()?.close?.lastOrNull()
                                    Text(
                                        text = price?.let {
                                            context.getString(R.string.stock_price_value, "%.2f".format(it))
                                        } ?: context.getString(R.string.stock_price_value, "%.2f".format(stockSymbol.stockPrice))
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp), // Optional padding between rows
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    val lastUpdatedTimeFormatted =
                                        stockSymbol.lastUpdatedTime?.let {
                                            dateFormat.format(Date(it))
                                        } ?: stringResource(id = R.string.unknown_time)
                                    Text(
                                        text = stringResource(id = R.string.last_updated_time, lastUpdatedTimeFormatted),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Cached,
                                contentDescription = "Localized description",
                                modifier = Modifier.clickable {
                                    stockPriceApiViewModel.fetchStockPriceResult(
                                        symbol = stockSymbol.stockSymbol,
                                        period1 = System.currentTimeMillis() / 1000 - 86400,
                                        period2 = System.currentTimeMillis() / 1000,
                                        marketCode = selectedStockMarket?.stockMarketCode ?: "",
                                        onSuccess = { response ->
                                            val result = response?.chart?.result?.firstOrNull()
                                            val meta = result?.meta

                                            val regularMarketPrice = meta?.regularMarketPrice
                                            val regularMarketDayHigh = meta?.regularMarketDayHigh
                                            val regularMarketDayLow = meta?.regularMarketDayLow
                                            val chartPreviousClose = meta?.chartPreviousClose
                                            val roundedPrice = regularMarketPrice?.let {
                                                decimalFormat.format(it).toDouble()
                                            }
//                                            val price = response?.chart?.result?.firstOrNull()?.indicators?.quote?.firstOrNull()?.close?.lastOrNull()
//                                            val roundedPrice = price?.let { decimalFormat.format(it).toDouble() }
                                            val currentTime = System.currentTimeMillis()
                                            var updatedName = stockSymbol.stockName
                                            if (stockSymbol.stockName == "") {
                                                stockPriceApiViewModel.searchStock(
                                                    stockSymbol.stockSymbol,
                                                    selectedStockMarket?.stockMarketCode ?: "",
                                                    onSuccess = { _ ->
                                                        val quotes = result?.meta
                                                        val shortName = quotes?.shortName
//                                                    updatedName = stockSymbol.stockName.ifEmpty {
//                                                        shortName ?: stockSymbol.stockName
//                                                    }
                                                        updatedName =
                                                            stockSymbol.stockName.takeIf { it.isNotEmpty() }
                                                                ?: shortName
                                                                        ?: stockSymbol.stockName
                                                        stockSymbolViewModel.insertStockSymbol(
                                                            StockSymbol(
                                                                stockSymbol = stockSymbol.stockSymbol,
                                                                stockName = updatedName,
                                                                stockMarket = stockSymbol.stockMarket,
                                                                stockPrice = roundedPrice,
                                                                regularMarketDayLow = regularMarketDayLow,
                                                                regularMarketDayHigh = regularMarketDayHigh,
                                                                chartPreviousClose = chartPreviousClose,
                                                                lastUpdatedTime = currentTime
                                                            )
                                                        )
                                                    })
                                            } else {
                                                stockSymbolViewModel.insertStockSymbol(
                                                    StockSymbol(
                                                        stockSymbol = stockSymbol.stockSymbol,
                                                        stockName = updatedName,
                                                        stockMarket = stockSymbol.stockMarket,
                                                        stockPrice = roundedPrice,
                                                        regularMarketDayLow = regularMarketDayLow,
                                                        regularMarketDayHigh = regularMarketDayHigh,
                                                        chartPreviousClose = chartPreviousClose,
                                                        lastUpdatedTime = currentTime
                                                    )
                                                )
                                            }

                                            scope.launch {
                                                snackbarHostState.showSnackbar(updateSuccessfulMessage)
                                            }
                                            showAddDialog = false
                                        },
                                        onError = { errorMessage ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar("$errorMessage")
                                            }
                                            showAddDialog = false
                                        }
                                    )
                                }
                            )
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
                    if (symbol.isNotEmpty()) {
                        stockPriceApiViewModel.fetchStockPriceResult(
                            symbol = symbol,
                            period1 = System.currentTimeMillis() / 1000 - 86400,
                            period2 = System.currentTimeMillis() / 1000,
                            marketCode = selectedStockMarket?.stockMarketCode ?: "",
                            onSuccess = { response ->
                                val result = response?.chart?.result?.firstOrNull()
                                val meta = result?.meta

                                val regularMarketPrice = meta?.regularMarketPrice
                                val regularMarketDayHigh = meta?.regularMarketDayHigh
                                val regularMarketDayLow = meta?.regularMarketDayLow
                                val chartPreviousClose = meta?.chartPreviousClose
                                val roundedPrice =
                                    regularMarketPrice?.let { decimalFormat.format(it).toDouble() }
                                val currentTime = System.currentTimeMillis()
                                var updatedName = name
                                if (name == "") {
                                    stockPriceApiViewModel.searchStock(
                                        symbol,
                                        selectedStockMarket?.stockMarketCode ?: "",
                                        onSuccess = { searchStockResponse ->
                                            val quotes = result?.meta
                                            val shortName = quotes?.shortName
                                            updatedName = name.ifEmpty {
                                                shortName ?: name
                                            }
                                            stockSymbolViewModel.insertStockSymbol(
                                                StockSymbol(
                                                    stockSymbol = symbol,
                                                    stockName = updatedName,
                                                    stockMarket = selectedMarketId,
                                                    stockPrice = roundedPrice,
                                                    regularMarketDayLow = regularMarketDayLow,
                                                    regularMarketDayHigh = regularMarketDayHigh,
                                                    chartPreviousClose = chartPreviousClose,
                                                    lastUpdatedTime = currentTime
                                                )
                                            )
                                        })
                                } else {
                                    stockSymbolViewModel.insertStockSymbol(
                                        StockSymbol(
                                            stockSymbol = symbol,
                                            stockName = updatedName,
                                            stockMarket = selectedMarketId,
                                            stockPrice = roundedPrice,
                                            regularMarketDayLow = regularMarketDayLow,
                                            regularMarketDayHigh = regularMarketDayHigh,
                                            chartPreviousClose = chartPreviousClose,
                                            lastUpdatedTime = currentTime
                                        )
                                    )
                                }



                                scope.launch {
                                    snackbarHostState.showSnackbar(addSuccessfulMessage)
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
                        stockSymbolViewModel.updateStockName(symbol, marketId, nameUpdate)
                        showUpdateDialog = false
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
                    text = stringResource(id = R.string.update_stock),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider()

                // 顯示股票代碼
                Text(
                    text = stringResource(id = R.string.stock_symbol, stockSymbol),
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
                    label = { Text(stringResource(id = R.string.stock_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onUpdate(stockSymbol, nameUpdate, stockMarket)
                        }
                    ) {
                        Text(stringResource(id = R.string.update))
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
    val name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.add_stock),
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
                    label = { Text(stringResource(id = R.string.stock_symbol)) },
                    modifier = Modifier.fillMaxWidth()
                )
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = { newName ->
//                        if (newName.length <= 40) {
//                            name = newName
//                        }
//                    },
//                    label = { Text("股票名稱") },
//                    modifier = Modifier.fillMaxWidth()
//                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val marketId = selectedStockMarket?.stockMarket ?: -1
                            onAdd(symbol, name, marketId)
                        }
                    ) {
                        Text(stringResource(id = R.string.tab_add))
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
                stringResource(id = R.string.stock_symbol),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Composable
fun StockMarketDropdown(
    stockMarkets: List<StockMarket>, // 替換為你的股票市場數據類型
    selectedStockMarket: StockMarket?,
    onStockMarketSelected: (StockMarket) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val options = SharedOptions.getOptionStockMarket(context)
    val myStockMarket = selectedStockMarket?.stockMarket ?: 0
    Column {
        OutlinedTextField(
            value = options[myStockMarket], // 假設 stockMarketName 是股票市場的顯示名稱
            onValueChange = {},
            label = { Text(stringResource(id = R.string.select_stock_market)) },
            readOnly = true,
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
                label = { Text(stringResource(id = R.string.search_stock_market)) },
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
                    text = { Text(options[stockMarket.stockMarket]) }, // 顯示股票市場名稱
                    onClick = {
                        onStockMarketSelected(stockMarket)
                        expanded = false
                    }
                )
            }
        }
    }
}