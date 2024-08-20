package com.banshus.mystock.ui.setting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.ui.tool.SwipeBox
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
){
    val stockAccounts by stockAccountViewModel.stockAccounts.observeAsState(emptyList())
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val updatedList = stockAccounts.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        stockAccountViewModel.updateStockAccountOrder(updatedList)
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableIntStateOf(0) }
    val recordCount by stockRecordViewModel.getRecordCountByAccountId(selectedAccountId).observeAsState(0)
    var selectedAccountName by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            AccountScreenHeader(navController)
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
                itemsIndexed(stockAccounts, key = { _, item -> item.accountId }) { _, item ->
                    ReorderableItem(reorderableState, item.accountId) {
                        var checked by remember { mutableStateOf(false) }
                        SwipeBox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            bottomContent = {
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .fillMaxHeight()
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .clickable {
//                                                stockAccount?.let { nonNullAccount ->
//                                                    stockViewModel.updateSelectedAccount(
//                                                        nonNullAccount
//                                                    )
//                                                }
//                                                stockViewModel.updateSelectedStock(record)
//                                                navController.navigate("stockDetailScreen")
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.align(Alignment.Center),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit"
                                            )
                                            Text(
                                                text = "編輯",
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .fillMaxHeight()
                                            .background(StockRed)
                                            .clickable {
                                                //彈跳Dialog視窗告知該帳戶有幾筆交易紀錄
//                                                stockAccountViewModel.deleteStockAccountById(item.accountId)
//                                                checked = false
                                                selectedAccountName = item.account
                                                selectedAccountId = item.accountId
                                                showDialog = true
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.align(Alignment.Center),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Delete"
                                            )
                                            Text(
                                                text = "刪除",
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            ListItem(
                                headlineContent = { Text(item.account) },
                                trailingContent = {
                                    Icon(
                                        Icons.Filled.DragHandle,
                                        contentDescription = "Reorder",
                                        modifier = Modifier
                                            .draggableHandle()
                                            .padding(10.dp)
                                    )
                                },
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
        if (showDialog) {
            DeleteAccountDialog(
                onDismissRequest = {
                    showDialog = false // Close the dialog when dismissed
                },
                onConfirmation = {
                    // Handle the confirmation action, e.g., delete the account
                    stockRecordViewModel.deleteAllRecordsByAccountId(selectedAccountId)
                    stockAccountViewModel.deleteStockAccountById(selectedAccountId)
                    showDialog = false // Close the dialog after the action is confirmed
                },
                recordCount = recordCount,
                selectedAccountName = selectedAccountName,
            )
        }
    }
}

@Composable
fun DeleteAccountDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    recordCount: Int,
    selectedAccountName: String
)
{
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "刪除「$selectedAccountName」",
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                Text(
                    text = "帳戶相關交易記錄會刪除",
                    modifier = Modifier.padding(4.dp),
                )
                Text(
                    text = "($recordCount)筆交易記錄",
                    modifier = Modifier.padding(4.dp),
                )
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("取消")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("刪除")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "股票帳戶",
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