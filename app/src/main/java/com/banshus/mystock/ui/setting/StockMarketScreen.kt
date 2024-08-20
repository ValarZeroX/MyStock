package com.banshus.mystock.ui.setting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.banshus.mystock.viewmodels.StockMarketViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StockMarketScreen(navController: NavHostController,stockMarketViewModel: StockMarketViewModel) {
//    val context = LocalContext.current
//    val stockMarketViewModel: StockMarketViewModel = viewModel(
//        factory = StockMarketViewModelFactory(
//            StockMarketRepository(AppDatabase.getDatabase(context).stockMarketDao())
//        )
//    )
    val stockMarketList by stockMarketViewModel.allStockMarkets.observeAsState(emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val updatedList = stockMarketList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        stockMarketViewModel.updateStockMarketsOrder(updatedList)
    }
    Scaffold(
        topBar = {
            StockMarketScreenHeader(navController, onAddClick = { showAddDialog = true })
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.padding(top = 10.dp),
                state = lazyListState
            ) {
                itemsIndexed(stockMarketList, key = { _, item -> item.stockMarket }) { _, item ->
                    ReorderableItem(reorderableState, item.stockMarket) {
                        ListItem(
                            headlineContent = { Text(item.stockMarketName) },
                            trailingContent = {
                                Icon(
                                    Icons.Filled.DragHandle,
                                    contentDescription = "Reorder",
                                    modifier = Modifier.draggableHandle().padding(10.dp)
                                )
                            },
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
//        if (showAddDialog) {
//            StockMarketAdd(
//                onDismiss = { showAddDialog = false },
//                onAdd = { name ->
//                    stockMarketViewModel.insert(StockMarket(stockMarketName = name, stockMarketSort = stockMarketList.size + 1))
//                    showAddDialog = false
//                }
//            )
//        }
    }
}

@Composable
fun StockMarketAdd(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
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
                    text = "新增市場",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { newName ->
                        if (newName.length <= 20) {
                            name = newName
                        }
                    },
                    label = { Text("市場名稱") },
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
                            if (name.isNotEmpty()) {
                                onAdd(name)
                            }
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
fun StockMarketScreenHeader(navController: NavHostController, onAddClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "股票市場",
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
//                actions = {
//                    IconButton(onClick = {  onAddClick() }) {
//                        Icon(
//                            imageVector = Icons.Filled.Add,
//                            contentDescription = "新增"
//                        )
//                    }
//                }
    )
}