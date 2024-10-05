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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.viewinterop.AndroidView
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.formatNumberNoDecimalPoint
import com.banshus.mystock.NumberUtils.getProfitColor
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.ui.tool.SwipeBox
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.tool.DateSwitcher
import com.banshus.mystock.viewmodels.BillingViewModel
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
    stockSymbolViewModel: StockSymbolViewModel,
    billingViewModel: BillingViewModel
) {
    val context = LocalContext.current
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

    val startDate by stockViewModel.startDate.observeAsState(LocalDate.now().withDayOfMonth(1))
    val endDate by stockViewModel.endDate.observeAsState(startDate.plusMonths(1).minusDays(1))

    val endDateTime = endDate.atTime(23, 59, 59)

    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDateMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val stockRecords by stockRecordViewModel.getStockRecordsByDateRangeAndAccount(
        accountId = selectedAccountForStockList?.accountId ?: -1,
        startDate = startDateMillis,
        endDate = endDateMillis
    ).observeAsState(initial = emptyList())

    //個股總成本、帳戶總成本
    val holdingsAndTotalCost by stockRecordViewModel.getHoldingsAndTotalCost(
        accountId = selectedAccountForStockList?.accountId ?: -1
    ).observeAsState(Pair(emptyMap(), 0.0))
    val (holdings, _) = holdingsAndTotalCost

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
    Scaffold(
        topBar = {
            StockListHeader(navController, stockAccount, stockViewModel, selectedTabIndex)
        },
        bottomBar = {
            AdBanner(billingViewModel) // 将广告放在底部栏
        }
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
                        text = { Text(stringResource(id = R.string.account_inventory)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(stringResource(id = R.string.transaction_record)) }
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
                                            text = stringResource(id = R.string.account_value),
                                            modifier = Modifier.weight(1f),
                                            fontSize = 24.sp
                                        )
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
                                        Text(text = stringResource(id = R.string.account_cost), modifier = Modifier.weight(1f))
                                        Text(text = stringResource(id = R.string.unrealized_gain_loss), modifier = Modifier.weight(1f))
                                        Text(text = stringResource(id = R.string.unrealized_gain_loss_percentage), modifier = Modifier.weight(1f))
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
                                    items(holdings.entries.filter { (_, holdingData) ->
                                        val (totalQuantity, _) = holdingData
                                        totalQuantity != 0
                                    }.toList()) { (stockSymbol, holdingData) ->
                                        val (totalQuantity, totalValue) = holdingData
                                        val stockName =
                                            stockSymbols.find { it.stockSymbol == stockSymbol }?.stockName
                                                ?: stringResource(id = R.string.unknown_stock_name)
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
                                            headlineContent = { Text(text = "$stockName($stockSymbol)") },
                                            supportingContent = {
                                                Column {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            stringResource(id = R.string.shares_held),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            stringResource(id = R.string.unit_cost),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            stringResource(id = R.string.total_cost),
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
                                                            stringResource(id = R.string.market_value),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            stringResource(id = R.string.profit_loss),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            stringResource(id = R.string.profit_loss_percentage),
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
                            onDateChanged = { start, end ->
                                stockViewModel.setDateRange(start, end)
                            }
                        )
                        LazyColumn {
                            items(stockRecords) { record ->
                                val transactionType = SharedOptions.getOptionsTransactionType(context)[record.transactionType]
                                val stockType = SharedOptions.getOptionsStockType(context)[record.stockType]
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
                                val stockName = stockSymbol?.stockName ?: stringResource(id = R.string.unknown_stock_name)
                                val priceName = SharedOptions.getPriceName(context, record.transactionType)
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
                                                        text = stringResource(id = R.string.edit),
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
                                                        text = stringResource(id = R.string.delete),
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    ListItem(
                                        headlineContent = { Text(text = "$stockName(${record.stockSymbol})") },
                                        supportingContent = {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(stringResource(id = R.string.quantity), modifier = Modifier.weight(1f))
                                                    Text(priceName, modifier = Modifier.weight(1f))
                                                    Text(stringResource(id = R.string.net_proceeds), modifier = Modifier.weight(1f))
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
                        contentDescription = "Add Stock List"
                    )
                }
            }
        }
    )
}

//@Composable
//fun StockPieChart(holdings: Map<String, Pair<Int, Double>>) {
//    //圖例文字顏色
//    val m3OnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
//    val m3Surface = MaterialTheme.colorScheme.surface.toArgb()
//    val entries = holdings.map { (stockSymbol, holdingData) ->
//        val (totalQuantity, _) = holdingData
//        PieEntry(totalQuantity.toFloat(), stockSymbol)
//    }
//
//    val dataSet = PieDataSet(entries, "Stock Holdings").apply {
//        colors = listOf(
//            Color.parseColor("#4777c0"),
//            Color.parseColor("#a374c6"),
//            Color.parseColor("#4fb3e8"),
//            Color.parseColor("#99cf43"),
//            Color.parseColor("#fdc135"),
//            Color.parseColor("#fd9a47"),
//            Color.parseColor("#eb6e7a"),
//            Color.parseColor("#6785c2")
//        )
//        setValueTextColors(colors)
//        valueLinePart1Length = 0.6f
//        valueLinePart2Length = 0.3f
//        valueLineWidth = 2f
//        valueLinePart1OffsetPercentage = 115f
//        isUsingSliceColorAsValueLineColor = true
//        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
//        valueTextSize = 16f
//        valueTypeface = Typeface.DEFAULT_BOLD
//        valueFormatter = object : ValueFormatter() {
//            private val formatter = NumberFormat.getPercentInstance()
//
//            override fun getFormattedValue(value: Float) =
//                formatter.format(value / 100f)
//        }
//    }
//
//    val pieData = PieData(dataSet)
//
//    AndroidView(
//        factory = { context ->
//            PieChart(context).apply {
//                this.data = pieData
//                this.description.isEnabled = false
//
//                //圖例
//                this.legend.isEnabled = false
//                this.legend.textColor = m3OnSurface
//                this.legend.textSize = 13f
//                this.setUsePercentValues(true)
////                this.setDrawHoleEnabled(true)
//                this.isDrawHoleEnabled = true
//                this.holeRadius = 70f
//                this.setHoleColor(m3Surface)
//
//
////                this.setHoleColor(Color.TRANSPARENT)  // 设置中央孔颜色为透明
////
////                // 确保透明圆设置正确
////                this.setTransparentCircleColor(Color.TRANSPARENT)
////                this.setTransparentCircleAlpha(0)
//
//                this.setDrawCenterText(true)
//                this.setCenterTextSize(14f)
////                this.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
//                this.setCenterTextColor(m3OnSurface)
//                this.centerText = "持有比例"
//                this.setExtraOffsets(0f, 20f, 0f, 20f)
////                this.renderer = CustomPieChartRenderer(this, 10f)
////                this.invalidate()
//            }
//        },
//        modifier = Modifier
//            .width(200.dp)  // 設定寬度
//            .height(200.dp) // 設定高度
////            .padding(40.dp)
//    )
//}