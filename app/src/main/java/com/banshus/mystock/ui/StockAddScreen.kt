package com.banshus.mystock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banshus.mystock.ui.theme.Black1
import com.banshus.mystock.ui.theme.White1
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.banshus.mystock.ui.theme.Black2
import com.banshus.mystock.ui.theme.Gary2
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.theme.Red1
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
fun StockAddScreen() {
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
    Column (
        modifier = Modifier
            .background(Black2)
            .fillMaxSize()
    ){
        Header()
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "股票代碼",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(100.dp)
                    .padding(start = 10.dp, end = 20.dp),
                color = White1
            )
            var stockSymbol by remember {
                mutableStateOf("")
            }
            OutlinedTextField(
                value = stockSymbol,
                onValueChange = {stockSymbol = it},
                label = {Text("股票代碼")},
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Black2, //背景
                    unfocusedContainerColor = Black2,
                    focusedIndicatorColor = Gray1, //邊線
                    unfocusedIndicatorColor = Gray1,
                    focusedLabelColor = Gary2, //標題
                    unfocusedLabelColor = Gary2,
                    cursorColor = Color.White, //光標顏色
                    focusedTextColor = White1, //字體
                    unfocusedTextColor = White1
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "股票名稱",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(100.dp)
                    .padding(start = 10.dp, end = 20.dp),
                color = White1
            )
            var stockName by remember {
                mutableStateOf("")
            }
            OutlinedTextField(
                value = stockName,
                onValueChange = {stockName = it},
                label = {Text("股票名稱")},
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Black2, //背景
                    unfocusedContainerColor = Black2,
                    focusedIndicatorColor = Gray1, //邊線
                    unfocusedIndicatorColor = Gray1,
                    focusedLabelColor = Gary2, //標題
                    unfocusedLabelColor = Gary2,
                    cursorColor = Color.White, //光標顏色
                    focusedTextColor = White1, //字體
                    unfocusedTextColor = White1
                ),
            )
        }
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "價格",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(100.dp)
                    .padding(start = 10.dp, end = 20.dp),
                color = White1
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
                label = {Text("價格")},
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Black2, //背景
                    unfocusedContainerColor = Black2,
                    errorContainerColor = Black2,
                    focusedIndicatorColor = Gray1, //邊線
                    unfocusedIndicatorColor = Gray1,
                    errorIndicatorColor = Red1,
                    focusedLabelColor = Gary2, //標題
                    unfocusedLabelColor = Gary2,
                    errorLabelColor = Red1,
                    cursorColor = Color.White, //光標顏色
                    focusedTextColor = White1, //字體
                    unfocusedTextColor = White1,
                    errorTextColor = White1
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                isError = isStockPriceError
            )
        }
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "股數",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(100.dp)
                    .padding(start = 10.dp, end = 20.dp),
                color = White1
            )
            var stockQuantity by remember {
                mutableStateOf("")
            }
            OutlinedTextField(
                value = stockQuantity,
                onValueChange = {stockQuantity = it},
                label = {Text("股數")},
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Black2, //背景
                    unfocusedContainerColor = Black2,
                    focusedIndicatorColor = Gray1, //邊線
                    unfocusedIndicatorColor = Gray1,
                    focusedLabelColor = Gary2, //標題
                    unfocusedLabelColor = Gary2,
                    cursorColor = Color.White, //光標顏色
                    focusedTextColor = White1, //字體
                    unfocusedTextColor = White1
                ),
            )
        }
        Row(modifier = Modifier
            .padding(10.dp)
        ) {
            Text(
                text = "交易日期",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(100.dp)
                    .padding(start = 10.dp, end = 20.dp),
                color = White1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
                ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Black2,
                        contentColor = White1
                    ),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Gary2,
                        shape = MaterialTheme.shapes.small
                    ).weight(1f),
                    onClick = {
                    calendarState.show()
                }) {
                    Text(stockDateTime)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Black2,
                        contentColor = White1,
                    ),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Gary2,
                        shape = MaterialTheme.shapes.small
                    ).weight(1f),
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
}
@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black1)
            .padding(top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "關閉",
            modifier = Modifier
                .padding(start = 16.dp)
                .size(36.dp),
            tint = White1
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "新增記錄",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = White1,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "確定",
            modifier = Modifier
                .padding(end = 16.dp)
                .size(36.dp),
            tint = White1
        )
    }
}

@Preview
@Composable
fun StockAddScreenPreview(){
    StockAddScreen()
}

