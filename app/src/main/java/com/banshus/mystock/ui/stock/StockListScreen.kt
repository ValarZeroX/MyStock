package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.repository.StockAccountRepository
import com.banshus.mystock.repository.StockRecordRepository
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModelFactory
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModelFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun StockListScreen(navController: NavHostController, stockViewModel: StockViewModel) {

    val selectedAccountForStockList by stockViewModel.selectedAccountForStockList.observeAsState()

    val context = LocalContext.current
    val stockAccountViewModel: StockAccountViewModel = viewModel(
        factory = StockAccountViewModelFactory(
            StockAccountRepository(AppDatabase.getDatabase(context).stockAccountDao())
        )
    )

    val stockRecordViewModel: StockRecordViewModel = viewModel(
        factory = StockRecordViewModelFactory(
            StockRecordRepository(AppDatabase.getDatabase(context).stockRecordDao())
        )
    )

    val stockAccount by stockAccountViewModel.getStockAccountByID(selectedAccountForStockList?.accountId ?: -1).observeAsState()

    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val startDate = currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDate = currentMonth.plusMonths(1).minusDays(1).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val stockRecords by stockRecordViewModel.getStockRecordsByDateRangeAndAccount(
        accountId = selectedAccountForStockList?.accountId ?: -1,
        startDate = startDate,
        endDate = endDate
    ).observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            StockListHeader(navController, stockAccount)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Column {
                MonthSwitcher { newMonth ->
                    currentMonth = newMonth
                }
                LazyColumn {
                    items(stockRecords) { record ->
                        // 根据 record 显示每个股票记录
                        Text(text = record.stockSymbol)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListHeader(navController: NavHostController, stockAccount: StockAccount?){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stockAccount?.account ?: "-",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "關閉"
                )
            }
        },
//        actions = {
//            IconButton(onClick = {
//                navController.navigate("addAccountScreen")
//            }) {
//                Icon(
//                    imageVector = Icons.Filled.Add,
//                    contentDescription = "新增"
//                )
//            }
//        }
    )
}

@Composable
fun MonthSwitcher(onMonthChanged: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            currentMonth = currentMonth.minusMonths(1)
            onMonthChanged(currentMonth)
        }) {
            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month")
        }

        Text(
            text = "${formatter.format(currentMonth)} ~ ${formatter.format(currentMonth.plusMonths(1).minusDays(1))}",
//            style = MaterialTheme.typography.body1
        )

        IconButton(onClick = {
            currentMonth = currentMonth.plusMonths(1)
            onMonthChanged(currentMonth)
        }) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month")
        }
    }
}