package com.banshus.mystock.ui.setting

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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.StockMarket
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.repository.StockMarketRepository
import com.banshus.mystock.repository.StockSymbolRepository
import com.banshus.mystock.ui.stock.Currency
import com.banshus.mystock.viewmodels.StockMarketViewModel
import com.banshus.mystock.viewmodels.StockMarketViewModelFactory
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModelFactory


@Composable
fun StockSymbolScreen(navController: NavHostController) {
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
    val stockMarketList by stockMarketViewModel.allStockMarkets.observeAsState(emptyList())
    var selectedStockMarket by remember { mutableStateOf<StockMarket?>(null) }

    // Initialize search query and search active state
    var showAddDialog by remember { mutableStateOf(false) }
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
    Scaffold(
        topBar = {
            StockSymbolScreenHeader(
                navController,
                onAddClick = { showAddDialog = true }
            )
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
                }

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
                        headlineContent = { Text(stockSymbol.stockSymbol) },
                        supportingContent = { Text(stockSymbol.stockName) },
                        trailingContent = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit",
                            )
                        },
                    )
                    HorizontalDivider()
                }
            }
        }
        if (showAddDialog) {
            StockSymbolAdd(
                stockMarketList = stockMarketList,
                onDismiss = { showAddDialog = false },
                onAdd = { symbol, name, selectedMarketId ->
                    if (symbol.isNotEmpty() && name.isNotEmpty()) {
                        stockSymbolViewModel.insertStockSymbol(
                            StockSymbol(
                                stockSymbol = symbol,
                                stockName = name,
                                stockMarket = selectedMarketId
                            )
                        )
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun StockSymbolAdd(
    stockMarketList: List<StockMarket>,
    onDismiss: () -> Unit,
    onAdd: (String, String, Int) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedMarketId by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    // 根据搜索查询过滤市场列表
    val filteredStockMarkets = stockMarketList.filter {
        it.stockMarketName.contains(searchQuery, ignoreCase = true)
    }
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
                        if (newName.length <= 20) {
                            symbol = newName
                        }
                    },
                    label = { Text("股票代碼") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { newName ->
                        if (newName.length <= 20) {
                            name = newName
                        }
                    },
                    label = { Text("股票名稱") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        searchQuery = newQuery
                    },
                    label = { Text("搜尋股票市場") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 显示股票市场列表
                LazyColumn {
                    items(filteredStockMarkets) { market ->
                        ListItem(
                            modifier = Modifier.clickable {
                                selectedMarketId = market.stockMarket
                            },
                            headlineContent = { Text(market.stockMarketName) },
                            trailingContent = {
                                if (selectedMarketId == market.stockMarket) {
                                    Icon(Icons.Filled.Check, contentDescription = "Selected")
                                }
                            }
                        )
                    }
                }

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
                            onAdd(symbol, name, selectedMarketId)
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
fun StockSymbolScreenHeader(navController: NavHostController, onAddClick: () -> Unit) {
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
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "新增"
                )
            }
        }
    )
}

@Composable
fun StockMarketDropdown(
    stockMarkets: List<StockMarket>, // 替換為你的股票市場數據類型
    selectedStockMarket: StockMarket?,
    onStockMarketSelected: (StockMarket) -> Unit
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
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.clickable { expanded = true }
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
                it.stockMarketName.contains(searchQuery, ignoreCase = true) // 假設 stockMarketName 是股票市場的顯示名稱
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