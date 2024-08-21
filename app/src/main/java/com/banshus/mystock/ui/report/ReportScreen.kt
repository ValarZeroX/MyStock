package com.banshus.mystock.ui.report

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.banshus.mystock.DateValueFormatter
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.getProfitColor
import com.banshus.mystock.SharedOptions.optionStockMarket
import com.banshus.mystock.SharedOptions.optionsStockType
import com.banshus.mystock.SharedOptions.optionsTransactionType
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.repository.RealizedTrade
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.theme.StockBlue
import com.banshus.mystock.ui.theme.StockGreen
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.ui.theme.StockText
import com.banshus.mystock.ui.tool.DateRangeType
import com.banshus.mystock.ui.tool.DateSwitcher
import com.banshus.mystock.ui.tool.RangeTypeSelectionDialog
import com.banshus.mystock.ui.tool.getStartAndEndDate
import com.banshus.mystock.viewmodels.DetailedStockMetrics
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun ReportScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel
) {
    val stockAccounts by stockAccountViewModel.stockAccountsMap.observeAsState(emptyMap())
    val firstStockAccount by stockAccountViewModel.firstStockAccount.observeAsState()
    val selectedAccount by stockViewModel.selectedAccount.observeAsState()
    var selectedAccountId by remember { mutableIntStateOf(0) }

    val accountText: String
    if (firstStockAccount != null) {
        if (selectedAccount == null) {
            accountText = firstStockAccount?.account ?: "No account selected"
            selectedAccountId = firstStockAccount?.accountId ?: 0
        } else {
            accountText = selectedAccount?.account ?: "No account selected"
            selectedAccountId = selectedAccount?.accountId ?: 0
        }
    } else {
        accountText = "No account selected"
    }
    Log.d("selectedAccountId","$selectedAccountId")
    Log.d("account","$stockAccounts")
    //DateSwitcher使用
//    var startDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
//    var endDate by remember { mutableStateOf(startDate.plusMonths(1).minusDays(1)) }


    val startDate by stockViewModel.startDate.observeAsState(LocalDate.now().withDayOfMonth(1))
    val endDate by stockViewModel.endDate.observeAsState(startDate.plusMonths(1).minusDays(1))

    val endDateTime = endDate.atTime(23, 59, 59)
    val currentRangeType by stockViewModel.currentRangeType.observeAsState(DateRangeType.MONTH)


    var selectedReportTabIndex by stockViewModel.selectedReportTabIndex
//    val showDialog by stockViewModel.showRangeTypeDialog.observeAsState(false)

    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDateMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

//    LaunchedEffect(startDate, endDate) {
//        Log.d("startDate", "$startDate")
//        Log.d("endDate", "$endDate")
//    }
    Log.d("startDateMillis", "$startDateMillis")
    Log.d("endDateMillis", "$endDateMillis")
//    val realizedTrades by stockRecordViewModel.getRealizedTradesForAllAccounts()
//        .observeAsState(emptyMap())
//
//    realizedTrades.forEach { (accountId, tradesBySymbol) ->
//        Log.d("tradesBySymbol", "$tradesBySymbol")
////        tradesBySymbol.forEach { (stockSymbol, trades) ->
////            trades.forEach { trade ->
////                // 这里可以展示每一笔买卖交易的详细信息
////                trade.buy.forEach { buyRecord ->
////                    // 展示买入记录
//////                    Log.d("buyRecord", "$buyRecord")
////                }
////                // 展示卖出记录
//////                Log.d("trades", "$trades")
////            }
////        }
//    }

    val allAccountsRecord by stockRecordViewModel.getFilteredRealizedTrades(
        startDateMillis,
        endDateMillis
    ).observeAsState(emptyMap())
//    val selectedAccount = 2

//    var isDataReady by remember { mutableStateOf(allAccountsRecord.isNotEmpty()) }
//
//    LaunchedEffect(allAccountsRecord) {
//        // 更新状态
//        isDataReady = allAccountsRecord.isNotEmpty()
//    }

    val accountMetrics by stockRecordViewModel.calculateMetricsForSelectedAccount(
        startDateMillis,
        endDateMillis,
        selectedAccountId
    ).observeAsState(DetailedStockMetrics(0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
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
    if (stockAccounts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "請建立帳戶")
        }
    } else if (allAccountsRecord.isEmpty()) {
        // 顯示加載動畫
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
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
                        onDateChanged = { start, end ->
                            stockViewModel.setDateRange(start, end)
                        }
                    )
                    when (selectedReportTabIndex) {
                        0 -> {
                            AccountTab(
                                allAccountsRecord[selectedAccountId],
                                accountMetrics,
                                navController,
                                accountText,
                                stockAccounts[selectedAccountId]!!,
                                currentRangeType
                            )
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
}

@Composable
fun AccountTab(
    map: Map<String, List<RealizedTrade>>?,
    accountMetrics: DetailedStockMetrics,
    navController: NavHostController,
    accountText: String,
    stockAccounts: StockAccount,
    currentRangeType: DateRangeType,
) {
    val profitColor = getProfitColor(
        accountMetrics.totalProfit,
        StockRed,
        StockGreen,
        MaterialTheme.colorScheme.onSurface
    )

    LazyColumn {
        item {
            Column(modifier = Modifier.padding(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AssistChip(
                        onClick = { navController.navigate("accountListScreen") },
                        label = { Text(accountText) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.AccountBalance,
                                contentDescription = "帳戶",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Gray1,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = optionStockMarket[stockAccounts.stockMarket],
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Gray1,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.AttachMoney,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stockAccounts.currency,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Row {
                    Text(text = "總買進", modifier = Modifier.weight(1f))
                    Text(text = "總賣出", modifier = Modifier.weight(1f))
                    Text(text = "總手續費", modifier = Modifier.weight(1f))
                }
                Row {
                    Text(
                        text = formatNumber(accountMetrics.totalCostBasis),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatNumber(accountMetrics.totalSellIncome),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatNumber(accountMetrics.totalCommission),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row {
                    Text(text = "總交易稅", modifier = Modifier.weight(1f))
                    Text(text = "總損益", modifier = Modifier.weight(1f))
                    Text(text = "總損益率", modifier = Modifier.weight(1f))
                }
                Row {
                    Text(
                        text = formatNumber(accountMetrics.totalTransactionTax),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatNumber(accountMetrics.totalProfit),
                        modifier = Modifier.weight(1f),
                        color = profitColor
                    )
                    Text(
                        text = "${formatNumber(accountMetrics.totalProfitPercent)}%",
                        modifier = Modifier.weight(1f),
                        color = profitColor
                    )
                }
            }
        }
        item {
            AccountMetricsLineChart(map,currentRangeType)
        }
        map?.forEach { (stockSymbol, realizedTrades) ->
            realizedTrades.forEach { trade ->
                item {
                    var isExpanded by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.padding(4.dp)) {
                        var buyTotal = 0.0
                        var tradeTotal = 0.0
                        var sellTotal = 0.0
                        trade.buy.forEach { record ->
                            buyTotal += record.quantity * record.pricePerUnit
                            tradeTotal += record.commission + record.transactionTax
                        }
                        sellTotal += trade.sell.quantity * trade.sell.pricePerUnit
                        tradeTotal += trade.sell.commission + trade.sell.transactionTax
                        val profitValue = sellTotal - buyTotal
                        val profitPercentValue = (profitValue / buyTotal) * 100
                        val profitColorStock = getProfitColor(
                            profitValue,
                            StockRed,
                            StockGreen,
                            MaterialTheme.colorScheme.onSurface
                        )
                        ListItem(
                            headlineContent = { Text(stockSymbol) },
                            supportingContent = {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "買進",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            "賣出",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            "交易費用",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            formatNumber(buyTotal),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = formatNumber(sellTotal),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            formatNumber(trade.sell.commission),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "損益",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            "損益率",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            "",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            formatNumber(profitValue),
                                            modifier = Modifier.weight(1f),
                                            color = profitColorStock
                                        )
                                        Text(
                                            "${formatNumber(profitPercentValue)}%",
                                            modifier = Modifier.weight(1f),
                                            color = profitColorStock
                                        )
                                        Text(
                                            "",
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                IconButton(onClick = { isExpanded = !isExpanded }) {
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand/Collapse"
                                    )
                                }
                            }
                        )
                        HorizontalDivider()
                        if (isExpanded) {
                            trade.buy.forEach { record ->
                                ListItemDetail(record = record)
                            }

                            ListItemDetail(record = trade.sell)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListItemDetail(record: StockRecord) {
    val transactionType = optionsTransactionType[record.transactionType]
    val stockType = optionsStockType[record.stockType]

    // 时间格式化
    val recordDateMillis = record.transactionDate
    val dateTime = Instant.ofEpochMilli(recordDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    val formatterDate = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val formattedDate = dateTime.format(formatterDate)
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTime = dateTime.format(formatterTime)

    ListItem(
        headlineContent = { Text(transactionType) },
        supportingContent = {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("股數", modifier = Modifier.weight(1f))
                    Text("每股價格", modifier = Modifier.weight(1f))
                    Text("手續費", modifier = Modifier.weight(1f))
                    Text("證交稅", modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${record.quantity}", modifier = Modifier.weight(1f))
                    Text(formatNumber(record.pricePerUnit), modifier = Modifier.weight(1f))
                    Text(formatNumber(record.commission), modifier = Modifier.weight(1f))
                    Text(formatNumber(record.transactionTax), modifier = Modifier.weight(1f))
                }
            }
        },
        trailingContent = {
            Column {
                Text(text = formattedDate)
                Text(text = formattedTime)
                Text(text = stockType)
            }
        }
    )
    HorizontalDivider()
}

@Composable
fun AccountMetricsLineChart(
    realizedTrades: Map<String, List<RealizedTrade>>?,
    currentRangeType: DateRangeType
) {
    val textColor = StockText.toArgb()
    val profitColor = StockRed.toArgb()
    val profitPercentColor = StockBlue.toArgb()
    // 创建数据集
    val profitEntries = mutableListOf<Entry>()
    val profitPercentEntries = mutableListOf<Entry>()

    // 用日期来分组交易记录
    val tradesByDate = mutableMapOf<Long, MutableList<RealizedTrade>>()
    realizedTrades?.forEach { (_, trades) ->
        trades.forEach { trade ->
            // 将时间戳转换为当天的开始时间（零点）
            val dateKey = when (currentRangeType) {
                DateRangeType.YEAR -> {
                    // 按月份分组，忽略具体日期，只保留年月
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = trade.sell.transactionDate
                        set(Calendar.DAY_OF_MONTH, 1)  // 设置为该月的第一天
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    calendar.timeInMillis
                }
                DateRangeType.MONTH -> {
                    // 按天分组
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = trade.sell.transactionDate
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    calendar.timeInMillis
                }
                else -> {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = trade.sell.transactionDate
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    calendar.timeInMillis
                }
            }
//            Log.d("dateKey","$dateKey")
            tradesByDate.getOrPut(dateKey) { mutableListOf() }.add(trade)
            // 将交易按照日期分组
//            if (tradesByDate.containsKey(dateKey)) {
//                tradesByDate[dateKey]?.add(trade)
//            } else {
//                tradesByDate[dateKey] = mutableListOf(trade)
//            }
        }
    }
    val sortedTradesByDate = tradesByDate.toSortedMap()
    sortedTradesByDate.forEach { (date, trades) ->
        Log.d("DateKey", "Date: $date, Trades: ${trades.size}")
    }
    // 计算每个日期的总損益和損益率
    sortedTradesByDate.forEach { (date, trades) ->
        var totalBuy = 0.0
        var totalSell = 0.0

        trades.forEach { trade ->
            trade.buy.forEach { record ->
                totalBuy += record.quantity * record.pricePerUnit
            }
            totalSell += trade.sell.quantity * trade.sell.pricePerUnit
        }

        val profitValue = totalSell - totalBuy
        val profitPercentValue = if (totalBuy != 0.0) (profitValue / totalBuy) * 100 else 0.0

        val xValue = date.toFloat()
        profitEntries.add(Entry(xValue, profitValue.toFloat()))
        profitPercentEntries.add(Entry(xValue, profitPercentValue.toFloat()))
    }
    // 创建数据集
    val profitDataSet = LineDataSet(profitEntries, "損益金額").apply {
        color = profitColor
        lineWidth = 2f
        valueTextColor = textColor
        valueTextSize = 8f
        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        setDrawFilled(true) // 啟用填充
        fillColor = profitColor // 設置填充顏色
        fillAlpha = 85 // 設置填充透明度 (0-255)
    }

//    val profitPercentDataSet = LineDataSet(profitPercentEntries, "損益率").apply {
//        color = profitPercentColor
//        lineWidth = 2f
//        axisDependency = YAxis.AxisDependency.RIGHT
//        valueTextColor = textColor
//        valueTextSize = 8f
//    }

    val lineData = LineData(profitDataSet)

    // 初始化图表
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                axisLeft.isEnabled = true
                legend.isEnabled = true
                xAxis.textColor = textColor
                axisLeft.textColor = textColor
                axisRight.textColor = textColor
                legend.textColor = textColor
                xAxis.valueFormatter = DateValueFormatter()

            }
        },
        update = { chart ->
            if (realizedTrades.isNullOrEmpty()) {
                chart.clear() // 清空图表数据
            } else {
                // 更新数据
                chart.data = lineData
            }
            chart.invalidate() // 刷新图表
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
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