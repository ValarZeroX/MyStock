package com.banshus.mystock.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.repository.StockSymbolRepository
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

    // Initialize search query and search active state
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    // Get the stock symbols list
    val stockSymbolList by stockSymbolViewModel.stockSymbolsListByMarket.observeAsState(emptyList())
    val stockMarketId = 0
    LaunchedEffect(stockMarketId) {
        stockSymbolViewModel.fetchStockSymbolsListByMarket(stockMarketId)
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue -> searchQuery = newValue },
                label = { Text("搜索股票") },
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(filteredStockSymbolList) { stockSymbol ->
                    ListItem(
                        headlineContent = { Text(stockSymbol.stockSymbol) },
                        supportingContent = { Text(stockSymbol.stockName)},
                        trailingContent = {
                            Icon(
                                Icons.Filled.DragHandle,
                                contentDescription = "Reorder",
                            )
                        },
                    )
                }
            }
        }
        if (showAddDialog) {
            StockSymbolAdd(
                onDismiss = { showAddDialog = false },
                onAdd = { symbol, name ->
                    if (symbol.isNotEmpty() && name.isNotEmpty()) {
                        stockSymbolViewModel.insertStockSymbol(StockSymbol(stockSymbol = symbol, stockName = name, stockMarket = 0))
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun StockSymbolAdd(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
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
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onAdd(symbol, name)
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