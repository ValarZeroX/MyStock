package com.banshus.mystock.ui.stock

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.ui.theme.StockGreen
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.viewinterop.AndroidView
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.formatNumberNoDecimalPoint
import com.banshus.mystock.NumberUtils.getProfitColor
import com.banshus.mystock.ui.tool.SwipeBox
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.tool.DateRangeType
import com.banshus.mystock.ui.tool.DateSwitcher
import com.banshus.mystock.ui.tool.MonthSwitcher
import com.banshus.mystock.viewmodels.StockMetrics
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.NumberFormat

@Composable
fun StockListScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel
) {
    val decimalFormat = DecimalFormat("#.00")
    val selectedAccountForStockList by stockViewModel.selectedAccountForStockList.observeAsState()

    val stockAccount by stockAccountViewModel.getStockAccountByID(
        selectedAccountForStockList?.accountId ?: -1
    ).observeAsState()

    val stockSymbols by stockSymbolViewModel.stockSymbolsListByMarket.observeAsState(emptyList())

    LaunchedEffect(stockSymbols) {
        stockRecordViewModel.setStockSymbols(stockSymbols)
    }

    val metrics by stockRecordViewModel.calculateTotalCostAndProfit(
        accountId = selectedAccountForStockList?.accountId ?: -1
    ).observeAsState(StockMetrics(0.0, 0.0, 0.0, 0.0))

//    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
//
//    val startDate = currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
//    val endDate =
//        currentMonth.plusMonths(1).minusDays(1).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault())
//            .toInstant().toEpochMilli()
    var startDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var endDate by remember { mutableStateOf(startDate.plusMonths(1).minusDays(1)) }
    val endDateTime = endDate.atTime(23, 59, 59)
    val currentRangeType by remember { mutableStateOf(DateRangeType.MONTH) }
    Log.d("startDate", "$startDate")
    Log.d("endDate", "$endDate")
    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDateMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    Log.d("startDateMillis", "$startDateMillis")
    Log.d("endDateMillis", "$endDateMillis")
    val stockRecords by stockRecordViewModel.getStockRecordsByDateRangeAndAccount(
        accountId = selectedAccountForStockList?.accountId ?: -1,
        startDate = startDateMillis,
        endDate = endDateMillis
    ).observeAsState(initial = emptyList())
    //帳戶全部交易紀錄
//    val stockRecordsAll by stockRecordViewModel.getStockRecordsByAccountId(
//        accountId = selectedAccountForStockList?.accountId ?: -1,
//    ).observeAsState(initial = emptyList())

    //個股總成本、帳戶總成本
    val holdingsAndTotalCost by stockRecordViewModel.getHoldingsAndTotalCost(
        accountId = selectedAccountForStockList?.accountId ?: -1
    ).observeAsState(Pair(emptyMap(), 0.0))
    val (holdings, _) = holdingsAndTotalCost
//    var isDataReady by remember { mutableStateOf(holdings.isNotEmpty()) }


    //已實現
//    val realizedGainsAndLosses by stockRecordViewModel.getRealizedGainsAndLosses(
//        accountId = selectedAccountForStockList?.accountId ?: -1
//    ).observeAsState(emptyMap())
//Log.d("realizedGainsAndLosses", "$realizedGainsAndLosses")
//
//    val totalRealizedResult = realizedGainsAndLosses.values.fold(RealizedResult(0.0, 0.0, 0.0, 0.0, 0.0)) { acc, result ->
//        RealizedResult(
//            buyCost = acc.buyCost + result.buyCost,
//            sellIncome = acc.sellIncome + result.sellIncome,
//            dividendIncome = acc.dividendIncome + result.dividendIncome,
//            totalCommission = acc.totalCommission + result.totalCommission,
//            totalTransactionTax = acc.totalTransactionTax + result.totalTransactionTax
//        )
//    }
//    Log.d("totalRealizedResult", "$totalRealizedResult")

//    LaunchedEffect(computedTotalPrice) {
//        totalPrice = computedTotalPrice
//    }

//    LaunchedEffect(holdings) {
//        Log.d("hold", "$holdings")
//        // 更新状态
//        isDataReady = holdings.isNotEmpty()
//    }


//    var selectedTabIndex by remember { mutableIntStateOf(0) }

    stockAccount?.let {
        stockSymbolViewModel.fetchStockSymbolsListByMarket(it.stockMarket)
    }

    val profitColor = getProfitColor(
        metrics.totalProfit,
        StockRed,
        StockGreen,
        MaterialTheme.colorScheme.onSurface
    )
    val profitPercentColor = getProfitColor(
        metrics.totalProfitPercent,
        StockRed,
        StockGreen,
        MaterialTheme.colorScheme.onSurface
    )

    var selectedTabIndex by stockViewModel.selectedTabIndex
//    Log.d("stockAccount", "$stockAccount")

//    val isLoading = remember { mutableStateOf(true) }
//
//    LaunchedEffect(stockAccount) {
//        if (stockAccount != null) {
//            isLoading.value = false
//        }
//    }
//
//    if (isLoading.value) {
//        // 显示加载指示器
//        CircularProgressIndicator()
//    } else {
//        // 显示数据
//        // 你的界面代码
//    }
    Scaffold(
        topBar = {
            StockListHeader(navController, stockAccount, stockViewModel, selectedTabIndex)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Column {
                // TabRow for switching between tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
//                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("帳戶庫存") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("交易紀錄") }
                    )
                }
                // Display content based on the selected tab
                when (selectedTabIndex) {
                    0 -> {
                        // Display content for "帳戶庫存" - placeholder content
                        Box {
                            Column {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "帳戶市值",
                                            modifier = Modifier.weight(1f),
                                            fontSize = 24.sp
                                        )
//                                        Box(
//                                            modifier = Modifier
//                                                .border(
//                                                    width = 1.dp,
//                                                    color = Gray1,
//                                                    shape = RoundedCornerShape(8.dp)
//                                                )
//                                                .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
//                                        ) {
//                                            Row(modifier = Modifier.padding(vertical = 3.dp),verticalAlignment = Alignment.CenterVertically) {
//                                                Text(
//                                                    text = optionStockMarket[stockAccount!!.stockMarket],
//                                                    fontSize = 14.sp,
//                                                    fontWeight = FontWeight.Bold
//                                                )
//                                            }
//                                        }
                                        Box(
                                            modifier = Modifier
                                                .border(
                                                    width = 1.dp,
                                                    color = Gray1,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(
                                                    top = 1.dp,
                                                    bottom = 1.dp,
                                                    start = 10.dp,
                                                    end = 10.dp
                                                )
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
                                                    text = "${stockAccount?.currency}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
//                                        AssistChip(
//                                            onClick = {  },
//                                            label = { Text("${stockAccount?.currency}") },
//                                            leadingIcon = {
//                                                Icon(
//                                                    Icons.Filled.AttachMoney,
//                                                    contentDescription = "Localized description",
//                                                    Modifier.size(AssistChipDefaults.IconSize)
//                                                )
//                                            }
//                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                    ) {
                                        Text(
                                            text = formatNumber(metrics.totalPrice),
                                            modifier = Modifier.weight(1f),
                                            fontSize = 24.sp
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "帳戶成本", modifier = Modifier.weight(1f))
                                        Text(text = "未實現損益", modifier = Modifier.weight(1f))
                                        Text(text = "未實現報酬率", modifier = Modifier.weight(1f))
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = formatNumber(metrics.totalCostBasis),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = formatNumber(metrics.totalProfit),
                                            modifier = Modifier.weight(1f),
                                            color = profitColor
                                        )
                                        Text(
                                            text = "${formatNumber(metrics.totalProfitPercent)}%",
                                            modifier = Modifier.weight(1f),
                                            color = profitPercentColor
                                        )
                                    }
                                }

//                                StockLineChart(stockRecords)
                                LazyColumn {
                                    items(holdings.entries.toList()) { (stockSymbol, holdingData) ->
//                                        Log.d(
//                                            "ttt",
//                                            "Stock: $stockSymbol, Total Value: $holdingData"
//                                        )
                                        val (totalQuantity, totalValue) = holdingData
                                        val stockName =
                                            stockSymbols.find { it.stockSymbol == stockSymbol }?.stockName
                                                ?: "未知股票名稱"
                                        val averageQuantity = totalValue / totalQuantity
                                        val formattedAverage = decimalFormat.format(averageQuantity)
                                        val currentPrice =
                                            stockSymbols.find { it.stockSymbol == stockSymbol }?.stockPrice
                                                ?: 0.0
                                        val marketValue = totalQuantity * currentPrice
                                        val profitValue = marketValue - totalValue
                                        val profitPercentValue = (profitValue / totalValue) * 100
                                        val profitValueColor = getProfitColor(
                                            profitValue,
                                            StockRed,
                                            StockGreen,
                                            MaterialTheme.colorScheme.onSurface
                                        )
                                        ListItem(
                                            headlineContent = { Text(text = "$stockSymbol ($stockName)") },
                                            supportingContent = {
                                                Column {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            "持有股數",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            "單位成本",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            "總成本",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            "$totalQuantity",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            formattedAverage,
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            formatNumber(totalValue),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            "市值",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            "損益",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            "損益率",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            formatNumber(marketValue),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            formatNumber(profitValue),
                                                            modifier = Modifier.weight(1f),
                                                            color = profitValueColor,
//                                                            style = TextStyle(
//                                                                fontWeight = FontWeight.Bold,
//                                                                fontSize = 16.sp
//                                                            )
                                                        )
                                                        Text(
                                                            "${formatNumber(profitPercentValue)}%",
                                                            modifier = Modifier.weight(1f),
                                                            color = profitValueColor,
//                                                            style = TextStyle(
//                                                                fontWeight = FontWeight.Bold,
//                                                                fontSize = 16.sp
//                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
//                        MonthSwitcher { newMonth ->
//                            currentMonth = newMonth
//                        }
                        DateSwitcher(
                            stockViewModel = stockViewModel,
                            initialDate = startDate,
                            initialRangeType = currentRangeType,
                            onDateChanged = { start, end ->
                                startDate = start
                                endDate = end
                            }
                        )
                        LazyColumn {
                            items(stockRecords) { record ->
                                val transactionType = when (record.transactionType) {
                                    0 -> "買入"
                                    1 -> "賣出"
                                    2 -> "股利"
                                    else -> "買入"
                                }
                                val stockType = when (record.stockType) {
                                    0 -> "一般"
                                    1 -> "ETF"
                                    2 -> "當沖"
                                    else -> "一般"
                                }
                                val textColor = when (record.transactionType) {
                                    0 -> StockGreen
                                    1 -> StockRed
                                    else -> MaterialTheme.colorScheme.onSurface // 默认颜色
                                }
                                val totalAmount = when (record.transactionType) {
                                    0 -> record.totalAmount * -1
                                    1 -> record.totalAmount
                                    2 -> record.totalAmount
                                    else -> record.totalAmount
                                }
                                val stockSymbol =
                                    stockSymbols.find { it.stockSymbol == record.stockSymbol }
                                val stockName = stockSymbol?.stockName ?: "未知股票名稱"
                                val priceName = when (record.transactionType) {
                                    0 -> "每股價格"
                                    1 -> "每股價格"
                                    2 -> "每股股利"
                                    else -> "每股價格"
                                }
                                //時間
                                val recordDateMillis = record.transactionDate
                                val dateTime = Instant.ofEpochMilli(recordDateMillis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                                val formatterDate =
                                    DateTimeFormatter.ofPattern("MMM dd, yyyy")
                                val formattedDate = dateTime.format(formatterDate)
                                val formatterTime =
                                    DateTimeFormatter.ofPattern("HH:mm")
                                val formattedTime = dateTime.format(formatterTime)
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
                                                        stockAccount?.let { nonNullAccount ->
                                                            stockViewModel.updateSelectedAccount(
                                                                nonNullAccount
                                                            )
                                                        }
                                                        stockViewModel.updateSelectedStock(record)
                                                        navController.navigate("stockDetailScreen")
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
                                                        stockRecordViewModel.deleteStockRecordById(record.recordId)
                                                        checked = false
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
                                        headlineContent = { Text(text = "${record.stockSymbol}($stockName)") },
                                        supportingContent = {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text("股數", modifier = Modifier.weight(1f))
                                                    Text(priceName, modifier = Modifier.weight(1f))
                                                    Text("淨收付", modifier = Modifier.weight(1f))
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        formatNumberNoDecimalPoint(record.quantity),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        "${record.pricePerUnit}",
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        formatNumber(totalAmount),
                                                        modifier = Modifier.weight(1f),
                                                        color = textColor
                                                    )
                                                }
                                            }
                                        },
                                        trailingContent = {
                                            Column {
                                                Text(
                                                    text = formattedDate
                                                )
                                                Text(
                                                    text = formattedTime
                                                )
                                                Text(
                                                    text = transactionType
                                                )
                                                Text(
                                                    text = stockType
                                                )
                                            }
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListHeader(
    navController: NavHostController,
    stockAccount: StockAccount?,
    stockViewModel: StockViewModel,
    selectedTabIndex: Int,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stockAccount?.account ?: "-",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            when (selectedTabIndex) {
                 0 -> IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = " back"
                    )
                }
                1 -> IconButton(onClick = {
                    stockViewModel.showDialog()
                }) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "range"
                    )
                }
            }
        },
        actions = {
            // Ensure stockAccount is not null before calling updateSelectedAccount
            stockAccount?.let {
                IconButton(onClick = {
                    stockViewModel.updateSelectedAccount(it)
                    navController.navigate("addStockScreen")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "add"
                    )
                }
            }
        }
    )
}

//@Composable
//fun StockLineChart(stockRecords: List<StockRecord>) {
//    // Convert StockRecord data to MPAndroidChart Entries
//    val entries = stockRecords.mapIndexed { index, record ->
//        Entry(index.toFloat(), record.pricePerUnit.toFloat())
//    }
//
//    val dataSet = LineDataSet(entries, "Stock Prices").apply {
//        color = ColorTemplate.COLORFUL_COLORS[0]
//        valueTextColor = ColorTemplate.COLORFUL_COLORS[0]
//        valueTextSize = 12f
//    }
//
//    val lineData = LineData(dataSet)
//
//    AndroidView(
//        factory = { context ->
//            LineChart(context).apply {
//                this.data = lineData
//                this.description.isEnabled = false
//                this.legend.isEnabled = true
//                this.xAxis.valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return "${value.toInt() + 1}"  // Custom format if needed
//                    }
//                }
//            }
//        },
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    )
//}


@Composable
fun StockPieChart(holdings: Map<String, Pair<Int, Double>>) {
    //圖例文字顏色
    val m3OnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val m3Surface = MaterialTheme.colorScheme.surface.toArgb()
    val entries = holdings.map { (stockSymbol, holdingData) ->
        val (totalQuantity, _) = holdingData
        PieEntry(totalQuantity.toFloat(), stockSymbol)
    }

    val dataSet = PieDataSet(entries, "Stock Holdings").apply {
        colors = listOf(
            Color.parseColor("#4777c0"),
            Color.parseColor("#a374c6"),
            Color.parseColor("#4fb3e8"),
            Color.parseColor("#99cf43"),
            Color.parseColor("#fdc135"),
            Color.parseColor("#fd9a47"),
            Color.parseColor("#eb6e7a"),
            Color.parseColor("#6785c2")
        )
        setValueTextColors(colors)
        valueLinePart1Length = 0.6f
        valueLinePart2Length = 0.3f
        valueLineWidth = 2f
        valueLinePart1OffsetPercentage = 115f
        isUsingSliceColorAsValueLineColor = true
        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        valueTextSize = 16f
        valueTypeface = Typeface.DEFAULT_BOLD
        valueFormatter = object : ValueFormatter() {
            private val formatter = NumberFormat.getPercentInstance()

            override fun getFormattedValue(value: Float) =
                formatter.format(value / 100f)
        }
    }

    val pieData = PieData(dataSet)

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                this.data = pieData
                this.description.isEnabled = false

                //圖例
                this.legend.isEnabled = false
                this.legend.textColor = m3OnSurface
                this.legend.textSize = 13f
                this.setUsePercentValues(true)
//                this.setDrawHoleEnabled(true)
                this.isDrawHoleEnabled = true
                this.holeRadius = 70f
                this.setHoleColor(m3Surface)


//                this.setHoleColor(Color.TRANSPARENT)  // 设置中央孔颜色为透明
//
//                // 确保透明圆设置正确
//                this.setTransparentCircleColor(Color.TRANSPARENT)
//                this.setTransparentCircleAlpha(0)

                this.setDrawCenterText(true)
                this.setCenterTextSize(14f)
//                this.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                this.setCenterTextColor(m3OnSurface)
                this.centerText = "持有比例"
                this.setExtraOffsets(0f, 20f, 0f, 20f)
//                this.renderer = CustomPieChartRenderer(this, 10f)
//                this.invalidate()
            }
        },
        modifier = Modifier
            .width(200.dp)  // 設定寬度
            .height(200.dp) // 設定高度
//            .padding(40.dp)
    )
}