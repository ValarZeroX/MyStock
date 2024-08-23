package com.banshus.mystock.ui.record

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.formatNumberNoDecimalPoint
import com.banshus.mystock.SharedOptions.optionStockMarket
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
import com.banshus.mystock.ui.tool.SwipeBox

@Composable
fun RecordScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockSymbolViewModel: StockSymbolViewModel,
    stockRecordViewModel: StockRecordViewModel,
) {
    val currentDate = Date()
    val calendar = remember { Calendar.getInstance() }
    var currentMonth by remember { mutableStateOf(calendar.time) }
    var selectedDate by remember { mutableStateOf(currentDate) }
    val selectedDateMillis = selectedDate.time
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

    val calendarMonth = Calendar.getInstance().apply {
        time = currentMonth
        set(Calendar.DAY_OF_MONTH, 1) // 设置为该月的第一天
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val monthStartDate = calendarMonth.timeInMillis // 获取该月的开始时间戳

    calendarMonth.add(Calendar.MONTH, 1) // 移动到下个月
    calendarMonth.add(Calendar.MILLISECOND, -1) // 回退一毫秒，获得本月的最后时刻
    val monthEndDate = calendarMonth.timeInMillis // 获取该月的结束时间戳
    val dateSerialNumberMap by stockRecordViewModel.getDateSerialNumberMapByDateRange(
        startDate = monthStartDate,
        endDate = monthEndDate
    ).observeAsState(initial = emptyMap())
//    val highlightDays = remember(dateSerialNumberMap) {
//        dateSerialNumberMap.values.toList()
//    }
//    var dates by remember { mutableStateOf(generateMonthDates(currentMonth, highlightDays)) }
    val highlightDays = remember(dateSerialNumberMap) {
        dateSerialNumberMap.values.toList()
    }

    // 初始化 dates 的状态，只在 highlightDays 准备好后生成
    var dates by remember(currentMonth, highlightDays) {
        mutableStateOf(generateMonthDates(currentMonth, highlightDays))
    }

    // 当 highlightDays 准备好时更新 dates
    LaunchedEffect(highlightDays) {
        dates = generateMonthDates(currentMonth, highlightDays)
    }

    val stockRecords by stockRecordViewModel.getStockRecordsByDateRange(
        startDate = startDate,
        endDate = endDateTime
    ).observeAsState(initial = emptyList())

    val stockSymbols by stockSymbolViewModel.allStockSymbols.observeAsState(emptyList())

    LaunchedEffect(stockSymbols) {
        stockRecordViewModel.setStockSymbols(stockSymbols)
    }

    val stockAccounts by stockAccountViewModel.stockAccountsMap.observeAsState(emptyMap())
//    LaunchedEffect(currentMonth, highlightDays) {
//        Log.d("LaunchedEffect", "$highlightDays")
//        dates = generateMonthDates(currentMonth, highlightDays)
//    }
//    LaunchedEffect(currentMonth, highlightDays) {
//        dates = generateMonthDates(currentMonth, highlightDays)
//    }

    Log.d("stockRecords", "$stockRecords")
    Log.d("dates", "$dates")
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
//                                                stockAccount?.let { nonNullAccount ->
//                                                    stockViewModel.updateSelectedAccount(
//                                                        nonNullAccount
//                                                    )
//                                                }
                                                stockSymbolViewModel.fetchStockSymbolsListByMarket(record.stockMarket)
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
                        ){
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
                                        text = stockAccounts[record.accountId]!!.account,
                                        modifier = Modifier
                                            .padding(4.dp)  // 添加一些内边距
                                            .border(
                                                width = 2.dp,  // 边框宽度
                                                color = MaterialTheme.colorScheme.primary,  // 边框颜色
                                                shape = RoundedCornerShape(50)  // 椭圆形边框
                                            )
                                            .clip(RoundedCornerShape(50))  // 将内容裁剪为椭圆形
                                            .padding(horizontal = 12.dp, vertical = 6.dp)  // 内部文本的填充
                                    )
                                    Text(
                                        text = optionStockMarket[record.stockMarket],
                                        modifier = Modifier
                                            .padding(4.dp)  // 添加一些内边距
                                            .border(
                                                width = 2.dp,  // 边框宽度
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(50)
                                            )
                                            .clip(RoundedCornerShape(50))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                                 )
                                    Text(
                                        text = transactionType,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 1.dp)
                                    )
                                    Text(
                                        text = stockType,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 1.dp)
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