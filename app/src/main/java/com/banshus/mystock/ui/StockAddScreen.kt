package com.banshus.mystock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.banshus.mystock.ui.stock.StockMainScreen
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAddScreen(navController: NavHostController) {
    //日期
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today: String = LocalDate.now().format(dateFormatter)
    var stockDateTime by remember { mutableStateOf(today) }
    val calendarState = rememberUseCaseState()
    //時間
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:00")
    val nowTime: String = LocalTime.now().format(timeFormatter)
    val selectedTime = remember { mutableStateOf(nowTime) }
    val clockState = rememberUseCaseState()
    Scaffold(
        topBar = {
            AddHeader()
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier.padding(20.dp)
                ) {
                    var stockSymbol by remember {
                        mutableStateOf("")
                    }
                    OutlinedTextField(
                        value = stockSymbol,
                        onValueChange = { stockSymbol = it },
                        label = { Text("股票代碼") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 5.dp)
                    )
                    var stockName by remember {
                        mutableStateOf("")
                    }
                    OutlinedTextField(
                        value = stockName,
                        onValueChange = { stockName = it },
                        label = { Text("股票名稱") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 5.dp)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(20.dp)
                ) {
                    var stockQuantity by remember {
                        mutableStateOf("")
                    }
                    var isStockQuantityError by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = stockQuantity,
                        onValueChange = { newText ->
                            val parsedValue = newText.toIntOrNull()
                            if (newText.isEmpty() || (parsedValue != null && parsedValue > 0)) {
                                stockQuantity = newText
                                isStockQuantityError = false
                            } else {
                                isStockQuantityError = true
                            }
                        },
                        label = { Text("股數") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isStockQuantityError,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 5.dp)
                    )
                    var stockPrice by remember {
                        mutableStateOf("")
                    }
                    var isStockPriceError by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = stockPrice,
                        onValueChange = { newText ->
                            // 驗證是否正浮點數
                            val parsedValue = newText.toFloatOrNull()
                            if (newText.isEmpty() || (parsedValue != null && parsedValue in 0f..1000000f)) {
                                stockPrice = newText
                                stockPrice = newText
                                isStockPriceError = false
                            } else {
                                isStockPriceError = true
                            }
                        },
                        label = { Text("每股價格") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isStockPriceError,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 5.dp)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = "交易類型",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    var selectedIndex by remember { mutableIntStateOf(0) }
                    val options = listOf("買入", "賣出", "股利")
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                onClick = { selectedIndex = index },
                                selected = index == selectedIndex,
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = "股票類型",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )

                    var selectedStockTypeIndex by remember { mutableIntStateOf(0) }
                    val options = listOf("一般", "ETF")
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                onClick = { selectedStockTypeIndex = index },
                                selected = index == selectedStockTypeIndex,
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = "交易日期",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                calendarState.show()
                            }) {
                            Text(stockDateTime)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                clockState.show()
                            }) {
                            Text(selectedTime.value)
                        }
                    }
                }
                CalendarDialog(
                    state = calendarState,
                    config = CalendarConfig(
                        yearSelection = true,
                        monthSelection = true,
                        style = CalendarStyle.MONTH,
                    ),
                    selection = CalendarSelection.Dates { newDates ->
                        stockDateTime = newDates.firstOrNull()?.toString() ?: ""
                    }
                )

                ClockDialog(
                    state = clockState,
                    selection = ClockSelection.HoursMinutes { hours, minutes ->
                        val newTime = LocalTime.of(hours, minutes, 0).format(timeFormatter)
                        selectedTime.value = newTime
                    },
                    config = ClockConfig(
                        defaultTime = LocalTime.parse(selectedTime.value, timeFormatter),
                        is24HourFormat = true
                    )
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = "交易帳戶",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    Button(
                        onClick = {
                        navController.navigate("addAccountScreen")
                        }) {
                        Text("國泰世華")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHeader() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "新增記錄",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "關閉"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "確認"
                )
            }
        }
    )
}
