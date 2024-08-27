package com.banshus.mystock.ui.stock

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.tool.DatePickerModal
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel
){
    val selectedStock by stockViewModel.selectedStock.observeAsState()
    val selectedAccount by stockViewModel.selectedAccount.observeAsState()
    Log.d("selectedAccount", "$selectedAccount")
    val stockSymbolList by stockSymbolViewModel.stockSymbolsListByMarket.observeAsState(emptyList())
    var selectedStockSymbol by remember { mutableStateOf<StockSymbol?>(null) }
    //股數
    var stockQuantity by remember { mutableStateOf("") }
    //每股價格
    var stockPrice by remember { mutableStateOf("") }
    var isStockQuantityError by remember { mutableStateOf(false) }
    var isStockPriceError by remember { mutableStateOf(false) }

    // 手續費
    var commission by remember { mutableStateOf("0") }
    var isCommissionError by remember { mutableStateOf(false) }
    // 證交稅
    var transactionTax by remember { mutableStateOf("0") }
    var isTransactionTaxError by remember { mutableStateOf(false) }

    //交易類別
    var selectedTransactionType by remember { mutableIntStateOf(0) }
    //股票類型
    var selectedStockTypeIndex by remember { mutableIntStateOf(0) }
    var selectedStockMarket by remember { mutableIntStateOf(0) }

    val priceName = when(selectedTransactionType) {
        0 -> "每股價格"
        1 -> "每股價格"
        else -> "每股股利"
    }
    val optionsTransactionType = SharedOptions.optionsTransactionType
    val optionsStockType = SharedOptions.optionsStockType

    //日期選擇棄
    val initialDate = selectedStock?.transactionDate ?: Calendar.getInstance().timeInMillis
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(initialDate) }

    val formattedDate = selectedDate?.let {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = it
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
    } ?: "選擇日期"

    //時間選擇器
    val initialDateTime = selectedStock?.transactionDate ?: System.currentTimeMillis()
    val calendar = Calendar.getInstance().apply {
        timeInMillis = initialDateTime
    }
    val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
    val initialMinute = calendar.get(Calendar.MINUTE)

    var selectedTime by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = initialHour,
                initialMinute = initialMinute,
                is24Hour = true
            )
        )
    }
    var showTimePicker by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val buttonText = run {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedTime.hour)
            set(Calendar.MINUTE, selectedTime.minute)
        }
        formatter.format(cal.time)
    }

    val combinedTimestamp: Long = combineDateAndTime(selectedDate, selectedTime, selectedStock!!.transactionDate)


//    val stockAccount by stockAccountViewModel.getStockAccountByID(
//        selectedStock!!.accountId ?: -1
//    ).observeAsState()
    //自動計算手續費
    var autoCalculateChecked by remember { mutableStateOf(false) }
//    var selectedAutoCalculate by remember { mutableStateOf(false) }
    var selectedCommissionDecimal by remember { mutableDoubleStateOf(0.0) }
    var selectedTransactionTax by remember { mutableDoubleStateOf(0.0) }
    var selectedDiscount by remember { mutableDoubleStateOf(0.0) }

    val decimalFormat = DecimalFormat("#.00")
    val calculatedCommission = remember(stockQuantity, stockPrice, selectedCommissionDecimal) {
        Log.d("stockQuantity", stockQuantity)
        Log.d("stockPrice", stockPrice)
        Log.d("selectedCommissionDecimal", "$selectedCommissionDecimal")
        Log.d("selectedDiscount", "$selectedDiscount")
        val quantity = stockQuantity.toDoubleOrNull() ?: 0.0
        val price = stockPrice.toDoubleOrNull() ?: 0.0
        val commissionPercent = selectedCommissionDecimal
        decimalFormat.format(quantity * price * commissionPercent * selectedDiscount)

    }

    val calculatedTransactionTax = remember(stockQuantity, stockPrice, selectedTransactionTax) {
        val quantity = stockQuantity.toDoubleOrNull() ?: 0.0
        val price = stockPrice.toDoubleOrNull() ?: 0.0
        val transactionTaxPercent = selectedTransactionTax
        decimalFormat.format(quantity * price * transactionTaxPercent * selectedDiscount)
    }
    // Update commission field automatically
    LaunchedEffect(autoCalculateChecked, selectedTransactionType, calculatedCommission, calculatedTransactionTax) {
        // 更新手續費
        commission = if (autoCalculateChecked && (selectedTransactionType == 0 || selectedTransactionType == 1)) {
            calculatedCommission.toString()
        } else {
            "0.0"
        }
        // 更新證交稅
        transactionTax = if (autoCalculateChecked && selectedTransactionType == 1) {
            calculatedTransactionTax.toString()
        } else {
            "0.0"
        }
        Log.d("selectedTransactionType", "$selectedTransactionType")
        Log.d("autoCalculateChecked", "$autoCalculateChecked")
        Log.d("commission", commission)
    }

    LaunchedEffect(selectedStock, stockSymbolList) {
        selectedStock?.let { stock ->
            stockQuantity = selectedStock!!.quantity.toString()
            stockPrice = selectedStock!!.pricePerUnit.toString()
            selectedStockSymbol = stockSymbolList.find { it.stockSymbol == stock.stockSymbol }
            selectedTransactionType = selectedStock!!.transactionType
            selectedStockTypeIndex = selectedStock!!.stockType
            commission = selectedStock!!.commission.toString()
            transactionTax = selectedStock!!.transactionTax.toString()
            selectedStockMarket = selectedStock!!.stockMarket
            selectedTransactionTax = selectedAccount!!.transactionTaxDecimal
            selectedCommissionDecimal = selectedAccount!!.commissionDecimal
            selectedDiscount = selectedAccount!!.discount
        }
    }

    LaunchedEffect(selectedStockMarket) {
        stockSymbolViewModel.fetchStockSymbolsListByMarket(selectedStockMarket)
    }

    Scaffold(
        topBar = {
            StockDetailHeader(
                navController,
                onSaveStockRecord = {
                    val stockRecord = StockRecord(
                        recordId = selectedStock!!.recordId,
                        accountId = selectedStock!!.accountId,
                        stockMarket = selectedStock!!.stockMarket,
                        stockSymbol = selectedStockSymbol?.stockSymbol ?: "",
                        stockType = selectedStockTypeIndex,
                        transactionType = selectedTransactionType,
                        transactionDate = combinedTimestamp,
                        quantity = stockQuantity.toInt(),
                        pricePerUnit = stockPrice.toDouble(),
                        totalAmount = stockQuantity.toInt() * stockPrice.toDouble(),
                        commission = commission.toDouble(),
                        transactionTax = transactionTax.toDouble(),
                        note = ""
                    )
                    stockRecordViewModel.updateStockRecord(stockRecord)
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "股票代碼",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(100.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        StockSymbolDropdown(
                            stockSymbols = stockSymbolList,
                            selectedStockSymbol = selectedStockSymbol,
                            navController = navController,
                            onStockSymbolSelected = {
                                selectedStockSymbol = it
                            }
                        )
                    }
                }
                item {
                    //台股支援自動計算
                    if (selectedStockMarket == 0 ) {
                        Row(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "自動計算",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .width(100.dp)
                                    .padding(start = 10.dp, end = 20.dp),
                            )
                            Switch(
                                checked = autoCalculateChecked,
                                onCheckedChange = {
                                    autoCalculateChecked = it
                                }
                            )
                        }
                        Row(modifier = Modifier.padding(10.dp)){
                            Text(
                                text = "只支援台股手續費、證交稅自動計算。",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp, end = 20.dp),
                                fontSize = 14.sp,
                                color = Gray1
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.padding(10.dp)
                    ) {
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
                            label = { Text(text = "股數")},
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = isStockQuantityError,
                            modifier = Modifier.weight(1f).padding(end = 5.dp)
                        )
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
                            label = { Text(text = priceName)},
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = isStockPriceError,
                            modifier = Modifier.weight(1f).padding(start = 5.dp)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        OutlinedTextField(
                            value = commission,
                            onValueChange = { newText ->
                                val parsedValue = newText.toFloatOrNull()
                                if (newText.isEmpty() || (parsedValue != null && parsedValue in 0f..1000000f)) {
                                    commission = newText
                                    isCommissionError = false
                                } else {
                                    isCommissionError = true
                                }
                            },
                            label = { Text(text = "手續費")},
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = isCommissionError,
                            enabled = !autoCalculateChecked,
                            modifier = Modifier.weight(1f).padding(end = 5.dp)
                        )
                        OutlinedTextField(
                            value = transactionTax,
                            onValueChange = { newText ->
                                val parsedValue = newText.toFloatOrNull()
                                if (newText.isEmpty() || (parsedValue != null && parsedValue in 0f..1000000f)) {
                                    transactionTax = newText
                                    isTransactionTaxError = false
                                } else {
                                    isTransactionTaxError = true
                                }
                            },
                            label = { Text(text = "證交稅")},
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = isTransactionTaxError,
                            enabled = !autoCalculateChecked,
                            modifier = Modifier.weight(1f).padding(start = 5.dp)
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
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            optionsTransactionType.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = optionsTransactionType.size
                                    ),
                                    onClick = { selectedTransactionType = index },
                                    selected = index == selectedTransactionType,
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
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            optionsStockType.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = optionsStockType.size
                                    ),
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
                                    showDatePicker = true
                                }) {
                                Text(formattedDate)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    showTimePicker = true
                                }) {
                                Text(buttonText)
                            }
                        }
                    }
                    if (showDatePicker) {
                        DatePickerModal(
                            selectedDate = selectedDate,
                            onDateSelected = { date ->
                                selectedDate = date
                                showDatePicker = false
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }
                    if (showTimePicker) {
                        AdvancedTimePickerExample(
                            selectedTime = selectedTime,
                            onDismiss = { showTimePicker = false },
                            onConfirm = { time ->
                                selectedTime = time
                                showTimePicker = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailHeader(
    navController: NavHostController,
    onSaveStockRecord: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "編輯交易紀錄",
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
                IconButton(onClick = {
                    onSaveStockRecord()
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "儲存"
                    )
                }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTimePickerExample(
    selectedTime: TimePickerState,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {

    val initialHour = selectedTime.hour
    val initialMinute = selectedTime.minute

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    /** Determines whether the time picker is dial or input */
    var showDial by remember { mutableStateOf(true) }

    /** The icon used for the icon button that switches from dial to input */
    val toggleIcon = if (showDial) {
        Icons.Filled.EditCalendar
    } else {
        Icons.Filled.AccessTime
    }

    AdvancedTimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) },
        toggle = {
            IconButton(onClick = { showDial = !showDial }) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = "Time picker type toggle",
                )
            }
        },
    ) {
        if (showDial) {
            TimePicker(
                state = timePickerState,
            )
        } else {
            TimeInput(
                state = timePickerState,
            )
        }
    }
}
@Composable
fun AdvancedTimePickerDialog(
    title: String = "選擇時間",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
            Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("取消") }
                    TextButton(onClick = onConfirm) { Text("確定") }
                }
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DatePickerModal(
//    selectedDate: Long?,
//    onDateSelected: (Long?) -> Unit,
//    onDismiss: () -> Unit
//) {
//    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = selectedDate
//    )
//
//
//    DatePickerDialog(
//        onDismissRequest = onDismiss,
//        confirmButton = {
//            TextButton(onClick = {
//                val selectedDateMillis = datePickerState.selectedDateMillis
//                onDateSelected(selectedDateMillis)
//                onDismiss()
//            }) {
//                Text("確定")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("取消")
//            }
//        }
//    ) {
//        DatePicker(
//            state = datePickerState
//        )
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
fun combineDateAndTime(selectedDate: Long?, selectedTime: TimePickerState, transactionDate: Long): Long {
    // 如果日期或时间为空，则返回 null
    if (selectedDate == null) return transactionDate

    // 创建 Calendar 实例
    val calendar = Calendar.getInstance().apply {
        // 设置日期部分
        timeInMillis = selectedDate
        // 设置时间部分
        set(Calendar.HOUR_OF_DAY, selectedTime.hour)
        set(Calendar.MINUTE, selectedTime.minute)
        set(Calendar.SECOND, 0) // 可选，设置秒钟
        set(Calendar.MILLISECOND, 0) // 可选，设置毫秒
    }

    // 返回时间戳
    return calendar.timeInMillis
}