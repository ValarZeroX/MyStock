package com.banshus.mystock.ui.record

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.formatNumberNoDecimalPoint
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.ui.theme.StockGreen
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import com.banshus.mystock.ui.tool.CalendarView

@Composable
fun RecordScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockSymbolViewModel: StockSymbolViewModel,
    stockRecordViewModel: StockRecordViewModel,
) {
//    val datePickerState = rememberDatePickerState()
//
//    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()

//    val startDate = Instant.ofEpochMilli(selectedDateMillis)
//        .atZone(ZoneId.systemDefault())
//        .toLocalDate()
//        .atStartOfDay(ZoneId.systemDefault())
//        .toInstant()
//        .toEpochMilli()
//
//    val endDateTime = Instant.ofEpochMilli(selectedDateMillis)
//        .atZone(ZoneId.systemDefault())
//        .toLocalDate()
//        .atTime(23, 59, 59)
//        .atZone(ZoneId.systemDefault())
//        .toInstant()
//        .toEpochMilli()

//    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
//    val endDateMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()





//    LaunchedEffect(stockSymbols) {
//        stockRecordViewModel.setStockSymbols(stockSymbols)
//    }

    val currentDate = Date()
    val calendar = remember { Calendar.getInstance() }
    var currentMonth by remember { mutableStateOf(calendar.time) }
    var selectedDate by remember { mutableStateOf(currentDate) }
    val selectedDateMillis = selectedDate.time
    val highlightDays = listOf(2, 14)  // 你想高亮的日期
    var dates by remember { mutableStateOf(generateMonthDates(currentMonth, highlightDays)) }

//    var dates = listOf(
//        Pair(currentDate, true),  // 将当前日期标记为 `true`（例如高亮）
//        // 添加其他日期
//    )
    val startDate = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val endDateTime = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atTime(23, 59, 59)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
    val stockRecords by stockRecordViewModel.getStockRecordsByDateRange(
        startDate = startDate,
        endDate = endDateTime
    ).observeAsState(initial = emptyList())
    val stockSymbols by stockSymbolViewModel.stockSymbolsListByMarket.observeAsState(emptyList())
    LaunchedEffect(stockSymbols) {
        stockRecordViewModel.setStockSymbols(stockSymbols)
    }
    Log.d("stockRecords", "$stockRecords")

    Log.d("selectedDate", "$selectedDate")
    Scaffold(
        topBar = {
            RecordScreenHeader(navController)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Column {

                CalendarView(
                    month = currentMonth,
                    date = dates,
                    displayNext = true,
                    displayPrev = true,
                    onClickNext = {
                        calendar.add(Calendar.MONTH, 1) // 将月份加一
                        currentMonth = calendar.time
                        dates = generateMonthDates(currentMonth, highlightDays) // 重新生成日期列表
                    },
                    onClickPrev = {
                        calendar.add(Calendar.MONTH, -1) // 将月份减一
                        currentMonth = calendar.time
                        dates = generateMonthDates(currentMonth, highlightDays) // 重新生成日期列表
                    },
                    onClick = { date ->
                        selectedDate = date // 更新选中的日期
//                        dates = dates.map { if (it.first == date) it.copy(second = true) else it.copy(second = false) }
                    },
                    startFromSunday = true,
//                    modifier = Modifier.fillMaxSize(),
                    selectedDate = selectedDate
                )
//                DatePicker(
//                    state = datePickerState,
//                    title = null,
//                    headline = null,
//                    showModeToggle = false,
//                )
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

                        ListItem(
                            headlineContent = { Text(text = "$stockName(${record.stockSymbol})") },
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

fun generateMonthDates(month: Date, highlightDays: List<Int>): List<Pair<Date, Boolean>> {
    val calendar = Calendar.getInstance().apply {
        time = month
        set(Calendar.DAY_OF_MONTH, 1)  // 设置为月份的第一天
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)  // 获取这个月的天数
    val dates = mutableListOf<Pair<Date, Boolean>>()

    for (day in 1..daysInMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val isHighlighted = day in highlightDays  // 检查当前日期是否在高亮日期列表中
        dates.add(Pair(calendar.time, isHighlighted))  // 根据是否高亮来设置 `true` 或 `false`
    }

    return dates
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
//        navigationIcon = {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(
//                    imageVector = Icons.Filled.ArrowBackIosNew,
//                    contentDescription = "返回"
//                )
//            }
//        },
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