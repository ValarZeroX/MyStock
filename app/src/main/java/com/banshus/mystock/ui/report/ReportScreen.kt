package com.banshus.mystock.ui.report

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.repository.RealizedTrade
import com.banshus.mystock.ui.tool.DateRangeType
import com.banshus.mystock.ui.tool.DateSwitcher
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun ReportScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel
) {
    //DateSwitcher使用
    var startDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var endDate by remember { mutableStateOf(startDate.plusMonths(1).minusDays(1)) }
    val endDateTime = endDate.atTime(23, 59, 59)
    val currentRangeType by remember { mutableStateOf(DateRangeType.MONTH) }

    var selectedReportTabIndex by stockViewModel.selectedReportTabIndex
//    val showDialog by stockViewModel.showRangeTypeDialog.observeAsState(false)

    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDateMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()


    val realizedTrades by stockRecordViewModel.getRealizedTradesForAllAccounts().observeAsState(emptyMap())

    realizedTrades.forEach { (accountId, tradesBySymbol) ->
        Log.d("tradesBySymbol", "$tradesBySymbol")
//        tradesBySymbol.forEach { (stockSymbol, trades) ->
//            trades.forEach { trade ->
//                // 这里可以展示每一笔买卖交易的详细信息
//                trade.buy.forEach { buyRecord ->
//                    // 展示买入记录
////                    Log.d("buyRecord", "$buyRecord")
//                }
//                // 展示卖出记录
////                Log.d("trades", "$trades")
//            }
//        }
    }

    val allAccountsRecord by stockRecordViewModel.getFilteredRealizedTrades(startDateMillis,endDateMillis).observeAsState(emptyMap())
    val selectedAccount = 2
//    // 处理获取到的数据，并展示在 UI 中
//    allAccountsRecord.forEach { (accountId, realizedTradesByStock) ->
//        Text(text = "Account ID: $accountId")
//        Log.d("Account", "$accountId")
//        realizedTradesByStock.forEach { (stockSymbol, trades) ->
//            Log.d("Stock", stockSymbol)
//            Log.d("Stock", "$trades")
////            trades.forEach { trade ->
////                Text(text = "Buy: ${trade.buy}")
////                Text(text = "Sell: ${trade.sell}")
////            }
//        }
//    }


//    stockRecordViewModel.loadRealizedGainsAndLossesForAllAccounts(startDateMillis, endDateMillis)
//    val allAccountsRecord by stockRecordViewModel.realizedGainsAndLossesForAllAccounts.observeAsState(emptyMap())
//    val accountId = 3
//    val accountRecord = allAccountsRecord[accountId]
//
//// 打印 accountId 为 3 的数据
//    Log.d("AccountRecord", "$accountRecord")
//
//    Log.d("startDate", "$startDate")
//    Log.d("endDate", "$endDate")
    Scaffold(
        topBar = {
            ReportHeader(stockViewModel)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Column {
                TabRow(
                    selectedTabIndex = selectedReportTabIndex,
//                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedReportTabIndex == 0,
                        onClick = { selectedReportTabIndex = 0 },
                        text = { Text("總覽") }
                    )
                    Tab(
                        selected = selectedReportTabIndex == 1,
                        onClick = { selectedReportTabIndex = 1 },
                        text = { Text("帳戶") }
                    )
                    Tab(
                        selected = selectedReportTabIndex == 2,
                        onClick = { selectedReportTabIndex = 2 },
                        text = { Text("市場") }
                    )
                    Tab(
                        selected = selectedReportTabIndex == 3,
                        onClick = { selectedReportTabIndex = 3 },
                        text = { Text("股票") }
                    )
                }
                DateSwitcher(
                    stockViewModel = stockViewModel,
                    initialDate = startDate,
                    initialRangeType = currentRangeType,
                    onDateChanged = { start, end ->
                        startDate = start
                        endDate = end
                    }
                )
                when (selectedReportTabIndex) {
                    0 -> {
                        Text("總覽")
                        AccountTab(allAccountsRecord[selectedAccount])
                    }
                    1 -> {
                        Text("帳戶")
                    }
                    2 -> {
                        Text("市場")
                    }
                    3 -> {
                        Text("股票")
                    }
                }
            }
        }
    }
}

@Composable
fun AccountTab(map: Map<String, List<RealizedTrade>>?) {
    LazyColumn {
        map?.forEach { (stockSymbol, realizedTrades) ->
            // 显示股票代码作为标题
            item {
                Text(
                    text = stockSymbol,
//                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(8.dp)
                )
            }

            realizedTrades.forEach { trade ->
                // 显示每个交易记录的详细信息
                item {
                    Column(modifier = Modifier.padding(4.dp)) {
                        trade.buy.forEach { record ->
                            ListItem(
                                headlineContent = { Text("Buy ${record.quantity} shares at ${record.pricePerUnit}") },
                                supportingContent = { Text("Commission: ${record.commission}, Transaction Tax: ${record.transactionTax}") }
                            )
                            HorizontalDivider()
                        }
                        ListItem(
                            headlineContent = { Text("Sell ${trade.sell.quantity} shares at ${trade.sell.pricePerUnit}") },
                            supportingContent = { Text("Commission: ${trade.sell.commission}, Transaction Tax: ${trade.sell.transactionTax}") }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHeader(
    stockViewModel: StockViewModel,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "報表",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                stockViewModel.showDialog()
            }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "range"
                )
            }
        },
//        actions = {
//            IconButton(onClick = {
//            }) {
//                Icon(
//                    imageVector = Icons.Filled.Save,
//                    contentDescription = "儲存"
//                )
//            }
//        }
    )
}