package com.banshus.mystock.ui.stock

import android.graphics.Color
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import com.banshus.mystock.CustomPieChartRenderer
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.data.entities.StockRecord
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat

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

    val stockSymbolViewModel: StockSymbolViewModel = viewModel(
        factory = StockSymbolViewModelFactory(
            StockSymbolRepository(AppDatabase.getDatabase(context).stockSymbolDao())
        )
    )

    val stockAccount by stockAccountViewModel.getStockAccountByID(
        selectedAccountForStockList?.accountId ?: -1
    ).observeAsState()

    val stockSymbols by stockSymbolViewModel.stockSymbolsListByMarket.observeAsState(emptyList())


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
    val (holdings, totalCost) = holdingsAndTotalCost
//    LaunchedEffect(holdingsAndTotalCost) {
//        println("Holdings: $holdingsAndTotalCost")
//    }
    //帳戶總成本
//    val totalCost by stockRecordViewModel.getTotalCost(
//        accountId = selectedAccountForStockList?.accountId ?: -1
//    ).observeAsState(initial = 0.0)

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    stockAccount?.let {
        stockSymbolViewModel.fetchStockSymbolsListByMarket(it.stockMarket)
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
                        text = { Text("交易紀錄") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("帳戶庫存") }
                    )
                }
                // Display content based on the selected tab
                when (selectedTabIndex) {
                    0 -> {
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

                    1 -> {
                        // Display content for "帳戶庫存" - placeholder content
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column {
                                //畫圓餅圖
                                StockPieChart(holdings)
                                Text(text = "$totalCost")
//                                StockLineChart(stockRecords)
                                LazyColumn {
                                    items(holdings.entries.toList()) { (stockSymbol, holdingData) ->
                                        val (totalQuantity, totalValue) = holdingData
                                        val stockName =
                                            stockSymbols.find { it.stockSymbol == stockSymbol }?.stockName
                                                ?: "未知股票名稱"

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
                                                            "$totalQuantity",
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            formatNumber(totalValue),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            formatNumber(totalValue),
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
        val (_, totalValue) = holdingData
        PieEntry(totalValue.toFloat(), stockSymbol)
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
                this.holeRadius = 50f
                this.setHoleColor(m3Surface)


//                this.setHoleColor(Color.TRANSPARENT)  // 设置中央孔颜色为透明
//
//                // 确保透明圆设置正确
//                this.setTransparentCircleColor(Color.TRANSPARENT)
//                this.setTransparentCircleAlpha(0)

                this.setDrawCenterText(true)
                this.setCenterTextSize(20f)
//                this.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                this.setCenterTextColor(m3OnSurface)
                this.centerText = "持有比例"
                this.setExtraOffsets(40f, 0f, 40f, 0f)
//                this.renderer = CustomPieChartRenderer(this, 10f)
//                this.invalidate()
            }
        },
        modifier = Modifier
            .width(400.dp)  // 設定寬度
            .height(400.dp) // 設定高度
            .padding(4.dp)
    )
}