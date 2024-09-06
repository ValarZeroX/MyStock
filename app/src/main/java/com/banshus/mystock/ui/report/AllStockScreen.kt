package com.banshus.mystock.ui.report

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
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

//    if (marketStockSummary != null) {
//        // 对 marketStockSummary 进行处理，显示或日志输出
//        Log.d("MarketStockSummary", "$marketStockSummary")
//    } else {
//        Log.d("MarketStockSummary", "No data for selected market")
//    }
//    Log.d("marketStockSummary", "$marketStockSummary")
    Log.d("selectedMarket", "$selectedMarket")
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
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
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
//                    Text(
//                        text = stockAccounts.currency,
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Bold
//                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            StockBuyPieChart(marketStockSummary)
        }
//        Row(modifier = Modifier.fillMaxWidth()) {
//            StockSellPieChart(marketStockSummary)
//        }
    }
}

@Composable
fun StockBuyPieChart(marketStockSummary: Map<String, StockSummary>?) {
    if (marketStockSummary.isNullOrEmpty()) {
        // 如果 marketStockSummary 是空的，顯示提示信息或占位符
        Text(text = "No data available", modifier = Modifier.fillMaxWidth(), fontSize = 16.sp)
        return
    }

    val m3OnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val m3Surface = MaterialTheme.colorScheme.surface.toArgb()

    // 将 `marketStockSummary` 转换为每个股票代码对应 `totalBuy` 的映射
    val totalBuyMap = marketStockSummary.mapValues { it.value.totalBuy.toFloat() }

    // 计算所有 `totalBuy` 的总和
    val totalBuySum = totalBuyMap.values.sum()

    // 使用 `totalBuy` 的比例创建 `PieEntry`
    val entries = totalBuyMap.map { (stockSymbol, totalBuy) ->
        PieEntry(totalBuy / totalBuySum * 100, stockSymbol) // 计算比例并转换为百分比
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
    Column {

        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    this.data = pieData
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
//                    this.legend.textColor = m3OnSurface
//                    this.legend.textSize = 15f
                    this.setUsePercentValues(true)
                    this.isDrawHoleEnabled = true
                    this.holeRadius = 60f
                    this.setHoleColor(m3Surface)
                    this.setDrawCenterText(true)
                    this.setCenterTextSize(14f)
                    this.setCenterTextColor(m3OnSurface)
//                this.centerText = "買進比例"
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
                            selectedEntryLabel.value = "" // 当没有选择时显示默认文本
                            this@apply.centerText = selectedEntryLabel.value
                        }
                    })
                }
            },
            modifier = Modifier
                .width(350.dp)
                .height(300.dp)
        )
        LegendList(totalBuyMap, entries, dataSet.colors, totalBuySum, selectedLabel.value)
    }
}

@Composable
fun LegendList(
    totalBuyMap: Map<String, Float>,
    entries: List<PieEntry>,
    colors: List<Int>,
    totalBuySum: Float,
    selectedLabel: String
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(entries.size) { index ->
            val entry = entries[index]
            val stockSymbol = entry.label
            val totalBuy = totalBuyMap[stockSymbol] ?: 0f
            ListItem(
                headlineContent = { Text(text = stockSymbol) },
                supportingContent = {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 8.dp)
                                .background(androidx.compose.ui.graphics.Color(colors[index]))
                        )
                        Text(
                            text = "$totalBuy (%.2f%%)".format((totalBuy / totalBuySum) * 100),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
//                trailingContent = { Text(text = stockMarketName) },
            )
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(bottom = 8.dp)
//            ) {
//                // 圓形顏色標誌
//                Box(
//                    modifier = Modifier
//                        .size(16.dp)
//                        .padding(end = 8.dp)
//                        .background(androidx.compose.ui.graphics.Color(colors[index]))
//                )
//                // 股票名稱
//                Text(
//                    text = stockSymbol,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.weight(1f) // 保持行对齐
//                )
//                // 股票的买入数值和百分比
//                Text(
//                    text = "$totalBuy (%.2f%%)".format((totalBuy / totalBuySum) * 100),
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Normal
//                )
//            }
        }
    }
}