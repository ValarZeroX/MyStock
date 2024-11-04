package com.banshus.mystock.ui.report

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.banshus.mystock.DateValueFormatter
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.repository.StockSummary
import com.banshus.mystock.viewmodels.StockMarketViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AllStockScreen(
    navController: NavHostController,
    stockRecordViewModel: StockRecordViewModel,
    stockViewModel: StockViewModel,
    stockMarketViewModel: StockMarketViewModel,
    stockAccounts: Map<Int, StockAccount>,
) {
    val context = LocalContext.current
    val startDate by stockViewModel.startDate.observeAsState(LocalDate.now().withDayOfMonth(1))
    val endDate by stockViewModel.endDate.observeAsState(startDate.plusMonths(1).minusDays(1))
    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDateMillis =
        endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val stockByMarket by stockRecordViewModel.getStockSummaryByMarketAndSymbol(
        startDate = startDateMillis,
        endDate = endDateMillis,
    ).observeAsState(emptyMap())
    val stockMarketList by stockMarketViewModel.allStockMarkets.observeAsState(emptyList())

    val selectedMarket by stockViewModel.selectedMarket.observeAsState()
    val marketToUse = selectedMarket ?: stockMarketList.firstOrNull()
    val stockMarketName = marketToUse?.let {
        SharedOptions.getOptionStockMarket(context)[it.stockMarket]
    } ?: "Unknown Market"

    val marketStockSummary = marketToUse?.let { stockByMarket[it.stockMarket] }
    var selectedShowType by remember { mutableIntStateOf(0) }
//    if (marketStockSummary != null) {
//        // 对 marketStockSummary 进行处理，显示或日志输出
//        Log.d("MarketStockSummary", "$marketStockSummary")
//    } else {
//        Log.d("MarketStockSummary", "No data for selected market")
//    }
//    Log.d("marketStockSummary", "$marketStockSummary")
//    Log.d("marketStockSummary", "$marketStockSummary")
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AssistChip(
                onClick = { navController.navigate("marketListScreen") },
                label = { Text(stockMarketName) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.AccountBalance,
                        contentDescription = "Stock Report",
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        )  {
            AssistChip(
                onClick = {
                    selectedShowType = 0
                },
                label = { Text(stringResource(id = R.string.transaction_type_buy)) },
                leadingIcon = {
                    if (selectedShowType == 0) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Stock Report buy",
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            AssistChip(
                onClick = {
                    selectedShowType = 1
                },
                label = { Text(stringResource(id = R.string.transaction_type_sell)) },
                leadingIcon = {
                    if (selectedShowType == 1) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Stock Report sell",
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            AssistChip(
                onClick = {
                    selectedShowType = 2
                },
                label = { Text(stringResource(id = R.string.transaction_type_dividend)) },
                leadingIcon = {
                    if (selectedShowType == 2) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Stock Report dividend",
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            StockBuyPieChart(marketStockSummary, selectedShowType)
        }
//        Row(modifier = Modifier.fillMaxWidth()) {
//            StockSellPieChart(marketStockSummary)
//        }
    }
}

@Composable
fun StockBuyPieChart(marketStockSummary: Map<String, StockSummary>?, selectedShowType: Int) {
    if (marketStockSummary.isNullOrEmpty()) {
        // 如果 marketStockSummary 是空的，顯示提示信息或占位符
        Box(
            modifier = Modifier.fillMaxSize(), // 讓 Box 充滿整個父佈局
            contentAlignment = Alignment.Center // 讓內容在水平方向和垂直方向都居中
        ) {
            Text(text = stringResource(id = R.string.no_data_available), fontSize = 16.sp)
        }
        return
    }

    val m3OnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val m3Surface = MaterialTheme.colorScheme.surface.toArgb()

    // 根據 selectedShowType 決定要顯示的數據類型
    val filteredSummary = when (selectedShowType) {
        0 -> marketStockSummary.filterValues { it.totalBuy > 0 } // 買進
        1 -> marketStockSummary.filterValues { it.totalSell > 0 } // 賣出
        2 -> marketStockSummary.filterValues { it.totalDividend > 0 } // 股利
        else -> emptyMap() // 如果沒有匹配，則不顯示
    }

    // 轉換為 PieEntry 只顯示所選類型的數據
    val totalAmountMap = when (selectedShowType) {
        0 -> filteredSummary.mapValues { it.value.totalBuy.toFloat() }
        1 -> filteredSummary.mapValues { it.value.totalSell.toFloat() }
        2 -> filteredSummary.mapValues { it.value.totalDividend.toFloat() }
        else -> emptyMap()
    }

    // 如果过滤後没有数据，则返回提示信息
    if (totalAmountMap.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.no_data_available), fontSize = 16.sp)
        }
        return
    }

    // 计算所有數量的总和
    val totalAmountSum = totalAmountMap.values.sum()

    // 使用百分比创建 PieEntry
    val entries = totalAmountMap.map { (stockSymbol, totalAmount) ->
        PieEntry(totalAmount / totalAmountSum * 100, stockSymbol) // 计算比例并转换为百分比
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
    val selectedEntryLabel = remember { mutableStateOf("") }
    val selectedLabel = remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            AndroidView(
                factory = { context ->
                    PieChart(context).apply {
                        this.data = pieData
                        this.description.isEnabled = false
                        this.legend.isEnabled = false
                        this.setUsePercentValues(true)
                        this.isDrawHoleEnabled = true
                        this.holeRadius = 60f
                        this.setHoleColor(m3Surface)
                        this.setDrawCenterText(true)
                        this.setCenterTextSize(14f)
                        this.setCenterTextColor(m3OnSurface)
                        this.centerText = selectedEntryLabel.value
                        this.setExtraOffsets(40f, 20f, 0f, 20f)
                        this.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                            override fun onValueSelected(e: Entry?, h: Highlight?) {
                                val entry = e as PieEntry
                                selectedEntryLabel.value = "${entry.value.toInt()}%"
                                selectedLabel.value = entry.label
                                this@apply.centerText = selectedEntryLabel.value // 更新 centerText
                            }

                            override fun onNothingSelected() {
                                selectedEntryLabel.value = ""
                                selectedLabel.value = ""
                                this@apply.centerText = selectedEntryLabel.value
                            }
                        })
                    }
                },
                update = { chart ->
                    // 根據選擇的類型過濾數據
                    val filteredMarketStockSummary = when (selectedShowType) {
                        0 -> marketStockSummary.filterValues { it.totalBuy > 0 } // 買進數據
                        1 -> marketStockSummary.filterValues { it.totalSell > 0 } // 賣出數據
                        2 -> marketStockSummary.filterValues { it.totalDividend > 0 } // 股利數據
                        else -> marketStockSummary // 預設顯示所有數據
                    }

                    if (filteredMarketStockSummary.isEmpty()) {
                        chart.clear()  // 如果沒有數據，清除圖表
                    } else {
                        // 更新圖表數據
                        val updatedTotalMap = when (selectedShowType) {
                            0 -> filteredMarketStockSummary.mapValues { it.value.totalBuy.toFloat() }
                            1 -> filteredMarketStockSummary.mapValues { it.value.totalSell.toFloat() }
                            2 -> filteredMarketStockSummary.mapValues { it.value.totalDividend.toFloat() }
                            else -> filteredMarketStockSummary.mapValues { it.value.totalBuy.toFloat() }
                        }

                        val updatedTotalSum = updatedTotalMap.values.sum()

                        val updatedEntries = updatedTotalMap.map { (stockSymbol, total) ->
                            PieEntry(total / updatedTotalSum * 100, stockSymbol) // 計算比例並轉換為百分比
                        }

                        // 創建新的 PieDataSet 和 PieData
                        val updatedDataSet = PieDataSet(updatedEntries, "Stock Holdings").apply {
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

                        // 創建新的 PieData
                        val updatedPieData = PieData(updatedDataSet)
                        chart.data = updatedPieData  // 更新圖表的數據
                    }
                    chart.invalidate()  // 刷新圖表
                },
                modifier = Modifier
                    .width(350.dp)
                    .height(300.dp)
            )
        }
        item{
            LegendList(totalAmountMap, entries, dataSet.colors, totalAmountSum, selectedLabel.value, selectedShowType)
        }
    }

}

@Composable
fun LegendList(
    totalBuyMap: Map<String, Float>,
    entries: List<PieEntry>,
    colors: List<Int>,
    totalBuySum: Float,
    selectedLabel: String,
    selectedShowType: Int
) {
    val filteredEntries = if (selectedLabel.isEmpty()) {
        entries // 如果没有选中的 label，显示所有条目
    } else {
        entries.filter { it.label == selectedLabel } // 只显示选中的条目
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        filteredEntries.forEachIndexed { index, entry ->
//            val entry = filteredEntries[index]  // 使用 filteredEntries，而不是 entries
            val stockSymbol = entry.label
            val totalBuy = totalBuyMap[stockSymbol] ?: 0f
            val labelText = when (selectedShowType) {
                0 -> stringResource(id = R.string.total_buy) // 顯示買進
                1 -> stringResource(id = R.string.total_sell) // 顯示賣出
                2 -> stringResource(id = R.string.total_dividend) // 顯示股利
                else -> stringResource(id = R.string.total_buy) // 默認顯示買進
            }
            ListItem(
                headlineContent = {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 8.dp)
                                .background(androidx.compose.ui.graphics.Color(colors[index]))
                        )
                        Text(text = stockSymbol)
                    }
                },
                supportingContent = {
                    Row {
                        Text(
                            text = labelText,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$totalBuy (%.2f%%)".format((totalBuy / totalBuySum) * 100),
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                },
            )
            HorizontalDivider()
        }
    }
}