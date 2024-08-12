package com.banshus.mystock.ui.stock

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.repository.StockAccountRepository
import com.banshus.mystock.repository.StockRecordRepository
import com.banshus.mystock.repository.StockSymbolRepository
import com.banshus.mystock.ui.theme.StockGreen
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModelFactory
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModelFactory
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.viewinterop.AndroidView
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.repository.RealizedResult
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

    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val startDate = currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDate =
        currentMonth.plusMonths(1).minusDays(1).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

    val stockRecords by stockRecordViewModel.getStockRecordsByDateRangeAndAccount(
        accountId = selectedAccountForStockList?.accountId ?: -1,
        startDate = startDate,
        endDate = endDate
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



    var selectedTabIndex by remember { mutableIntStateOf(0) }

    stockAccount?.let {
        stockSymbolViewModel.fetchStockSymbolsListByMarket(it.stockMarket)
    }

    val profitColor = when {
        metrics.totalProfit > 0 -> StockRed
        metrics.totalProfit < 0 -> StockGreen
        else -> MaterialTheme.colorScheme.onSurface
    }

    val profitPercentColor = when {
        metrics.totalProfitPercent > 0 -> StockRed
        metrics.totalProfitPercent < 0 -> StockGreen
        else -> MaterialTheme.colorScheme.onSurface
    }

    Scaffold(
        topBar = {
            StockListHeader(navController, stockAccount, stockViewModel)
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
                                        Text(text = "帳戶市值", modifier = Modifier.weight(1f), fontSize = 24.sp)
                                        AssistChip(
                                            onClick = {  },
                                            label = { Text("${stockAccount?.currency}") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Filled.AttachMoney,
                                                    contentDescription = "Localized description",
                                                    Modifier.size(AssistChipDefaults.IconSize)
                                                )
                                            }
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                                    ) {
                                        Text(text = formatNumber(metrics.totalPrice), modifier = Modifier.weight(1f), fontSize = 24.sp)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "帳戶成本", modifier = Modifier.weight(1f))
                                        Text(text = "未實現損益", modifier = Modifier.weight(1f))
                                        Text(text = "未實現損益率", modifier = Modifier.weight(1f))
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = formatNumber(metrics.totalCostBasis), modifier = Modifier.weight(1f))
                                        Text(text = formatNumber(metrics.totalProfit), modifier = Modifier.weight(1f), color = profitColor)
                                        Text(text = "${formatNumber(metrics.totalProfitPercent)}%", modifier = Modifier.weight(1f), color = profitPercentColor )
                                    }
//                                    Row(
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        Text(text = "已實現損益", modifier = Modifier.weight(1f))
//                                        Text(text = "股利", modifier = Modifier.weight(1f))
//                                    }
//                                    Row(
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        Text(text = formatNumber(totalRealizedResult.sellIncome), modifier = Modifier.weight(1f))
//                                        Text(text = formatNumber(totalRealizedResult.dividendIncome), modifier = Modifier.weight(1f))
//                                    }
                                }
//                                Row {
//
//                                }
//                                Column(modifier = Modifier.padding(16.dp)) {
//                                    Row {
//                                        Text(text = "总成本: ")
//                                        Text(text = formatNumber(totalExpenditure))
//                                    }
//                                    Row {
//                                        Text(text = "帐户现值: ")
//                                        Text(text = formatNumber(totalMarketValue))
//                                    }
//                                    Row {
//                                        Text(text = "支出: ")
//                                        Text(text = formatNumber(totalExpenditure))
//                                    }
//                                    Row {
//                                        Text(text = "收入: ")
//                                        Text(text = formatNumber(totalIncome))
//                                    }
//                                    Row {
//                                        Text(text = "现现金利: ")
//                                        Text(text = formatNumber(dividendIncome))
//                                    }
//                                    Row {
//                                        Text(text = "已实现损益: ")
//                                        Text(text = formatNumber(totalRealizedProfit))
//                                    }
//                                    Row {
//                                        Text(text = "库存损益: ")
//                                        Text(text = formatNumber(totalMarketValue - totalExpenditure))
//                                    }
//                                    Row {
//                                        Text(text = "总手續費成本: ")
//                                        Text(text = formatNumber(totalCommission))
//                                    }
//                                    Row {
//                                        Text(text = "总交易税成本: ")
//                                        Text(text = formatNumber(totalTransactionTax))
//                                    }
//                                }
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(8.dp),
////                                        .height(IntrinsicSize.Min),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.Center
//                                ){
//                                    Box(
//                                        modifier = Modifier
//                                            .weight(1f)
////                                            .aspectRatio(1f)  // 保持圓餅圖的長寬比例為1:1
//                                            .align(Alignment.CenterVertically)
//                                    ) {
//                                        //畫圓餅圖
//                                        if (isDataReady){
//                                            StockPieChart(holdings)
//                                        } else {
//                                            Text(text = "Loading")
//                                        }
//                                    }
//                                    Text(text = "$totalCost")
//                                }

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
//                                        totalPrice += marketValue
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
                                                        Text(
                                                            "市值",
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
                                                        Text(
                                                            formatNumber(marketValue),
                                                            modifier = Modifier.weight(1f)
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
                        MonthSwitcher { newMonth ->
                            currentMonth = newMonth
                        }
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
                                                    formatNumber(record.quantity),
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
                                                text = "${
                                                    Instant.ofEpochMilli(record.transactionDate)
                                                        .atZone(ZoneId.systemDefault())
                                                        .toLocalDate()
                                                }"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListHeader(
    navController: NavHostController,
    stockAccount: StockAccount?,
    stockViewModel: StockViewModel
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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "關閉"
                )
            }
        },
        actions = {
            // Ensure stockAccount is not null before calling updateSelectedAccount
            stockAccount?.let {
                IconButton(onClick = {
                    stockViewModel.updateSelectedAccount(it)
                    navController.navigate("stockAddScreen")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "新增"
                    )
                }
            }
        }
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
            text = "${formatter.format(currentMonth)} ~ ${
                formatter.format(
                    currentMonth.plusMonths(1).minusDays(1)
                )
            }",
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