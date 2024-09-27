package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.tool.DatePickerModal
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel
) {
    val context = LocalContext.current
    //撈第一筆帳戶
    val firstStockAccount by stockAccountViewModel.firstStockAccount.observeAsState()

    //日期選擇棄
    val initialDate = Calendar.getInstance().timeInMillis
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
    } ?: stringResource(id = R.string.select_date)

    //時間選擇器
    val initialDateTime = System.currentTimeMillis()
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
//    //日期
//    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//    val today: String = LocalDate.now().format(dateFormatter)
//    var stockDateTime by remember { mutableStateOf(today) }
//    val calendarState = rememberUseCaseState()
//    //時間
//    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:00")
//    val nowTime: String = LocalTime.now().format(timeFormatter)
//    val selectedTime = remember { mutableStateOf(nowTime) }
//    val clockState = rememberUseCaseState()
    // 手續費
    var commission by remember { mutableStateOf("0") }
    var isCommissionError by remember { mutableStateOf(false) }
    // 證交稅
    var transactionTax by remember { mutableStateOf("0") }
    var isTransactionTaxError by remember { mutableStateOf(false) }

    val selectedAccount by stockViewModel.selectedAccount.observeAsState()

    //帳戶
    var selectedAccountId by remember { mutableIntStateOf(0) }
    //股票市場
    var selectedStockMarket by remember { mutableIntStateOf(0) }
    //股票類型
    var selectedStockTypeIndex by remember { mutableIntStateOf(0) }
    //交易類別
    var selectedTransactionType by remember { mutableIntStateOf(0) }
    //交易時間
//    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00")
//    val dateTimeString = "$stockDateTime ${selectedTime.value}"
//    val localDateTime = LocalDateTime.parse(dateTimeString, dateTimeFormatter)
//    val instant = localDateTime.toInstant(ZoneOffset.UTC)
//    val transactionDate: Long = instant.toEpochMilli()
    val transactionDate: Long = combineDateAndTimeVersion2(selectedDate, selectedTime)
    //股數
    var stockQuantity by remember { mutableStateOf("") }
    //每股價格
    var stockPrice by remember { mutableStateOf("") }

    val quantityInt = stockQuantity.toIntOrNull() ?: 0
    val pricePerUnitDouble = stockPrice.toDoubleOrNull() ?: 0.0
    val commissionDouble = commission.toDoubleOrNull() ?: 0.0
    val transactionTaxDouble = transactionTax.toDoubleOrNull() ?: 0.0

    //撈股票代碼
    LaunchedEffect(selectedStockMarket) {
        stockSymbolViewModel.fetchStockSymbolsListByMarket(selectedStockMarket)
    }
    val stockSymbolList by stockSymbolViewModel.stockSymbolsListByMarket.observeAsState(emptyList())
    var selectedStockSymbol by remember { mutableStateOf<StockSymbol?>(null) }

    //自動計算手續費
    var autoCalculateChecked by remember { mutableStateOf(false) }
    var selectedAutoCalculate by remember { mutableStateOf(false) }
    var selectedCommissionDecimal by remember { mutableDoubleStateOf(0.0) }
    var selectedTransactionTax by remember { mutableDoubleStateOf(0.0) }
    var selectedDiscount by remember { mutableDoubleStateOf(0.0) }

    val decimalFormat = DecimalFormat("#.00")
//     Calculate commission based on quantity, price and commission percentage
    val calculatedCommission = remember(stockQuantity, stockPrice, selectedCommissionDecimal) {
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
    LaunchedEffect(
        autoCalculateChecked,
        selectedTransactionType,
        calculatedCommission,
        calculatedTransactionTax
    ) {
        // 更新手續費
        commission =
            if (autoCalculateChecked && (selectedTransactionType == 0 || selectedTransactionType == 1)) {
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
    }

    val priceName = SharedOptions.getPriceName(context, selectedTransactionType)
    val accountText: String
    if (firstStockAccount != null) {
        if (selectedAccount == null) {
            accountText = firstStockAccount?.account ?: "No account selected"
            selectedAccountId = firstStockAccount?.accountId ?: 0
            selectedStockMarket = firstStockAccount?.stockMarket ?: 0
            selectedAutoCalculate = firstStockAccount?.autoCalculate ?: false
            selectedCommissionDecimal = firstStockAccount?.commissionDecimal ?: 0.0
            selectedTransactionTax = firstStockAccount?.transactionTaxDecimal ?: 0.0
            selectedDiscount = firstStockAccount?.discount ?: 1.0
        } else {
            accountText = selectedAccount?.account ?: "No account selected"
            selectedAccountId = selectedAccount?.accountId ?: 0
            selectedStockMarket = selectedAccount?.stockMarket ?: 0
            selectedAutoCalculate = selectedAccount?.autoCalculate ?: false
            selectedCommissionDecimal = selectedAccount?.commissionDecimal ?: 0.0
            selectedTransactionTax = selectedAccount?.transactionTaxDecimal ?: 0.0
            selectedDiscount = selectedAccount?.discount ?: 1.0
        }
    } else {
        accountText = "No account selected"
    }


    var isStockQuantityError by remember { mutableStateOf(false) }
    var isStockPriceError by remember { mutableStateOf(false) }
    val optionsTransactionType = SharedOptions.getOptionsTransactionType(context)
    val optionsStockType = SharedOptions.getOptionsStockType(context)
    Scaffold(
        topBar = {
            AddHeader(
                onSaveStockRecord = {
                    val stockRecord = StockRecord(
                        accountId = selectedAccountId,
                        stockMarket = selectedStockMarket,
                        stockSymbol = selectedStockSymbol?.stockSymbol ?: "",
                        stockType = selectedStockTypeIndex,
                        transactionType = selectedTransactionType,
                        transactionDate = transactionDate,
                        quantity = quantityInt,
                        pricePerUnit = pricePerUnitDouble,
                        totalAmount = quantityInt * pricePerUnitDouble,
                        commission = commissionDouble,
                        transactionTax = transactionTaxDouble,
                        note = ""
                    )
                    stockRecordViewModel.insertStockRecord(stockRecord)
                },
                navController
            )
        },
        bottomBar = {
            AdBanner() // 将广告放在底部栏
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.trading_account),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(110.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    if (firstStockAccount == null) {
                        AssistChip(
                            onClick = { navController.navigate("addAccountScreen") },
                            label = { Text(stringResource(id = R.string.add_account)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "新增帳戶",
                                    Modifier.size(AssistChipDefaults.IconSize)
                                )
                            }
                        )
                    } else {
                        AssistChip(
                            onClick = { navController.navigate("accountListScreen") },
                            label = { Text(accountText) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.AccountBalance,
                                    contentDescription = "帳戶",
                                    Modifier.size(AssistChipDefaults.IconSize)
                                )
                            }
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.stock_symbol),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(110.dp)
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
                if (selectedStockMarket == 0) {
                    Row(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.auto_calculate),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(110.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        Switch(
                            checked = autoCalculateChecked,
                            onCheckedChange = {
                                autoCalculateChecked = it
                            }
                        )
                    }
                    Row(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = stringResource(id = R.string.taiwan_stock_support_note),
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
                        label = { Text(text = stringResource(id = R.string.quantity)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isStockQuantityError,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 5.dp)
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
                        label = { Text(text = priceName) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal // 显示带有小数点的数字键盘
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
                        label = { Text(text = stringResource(id = R.string.commission)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal // 显示带有小数点的数字键盘
                        ),
                        isError = isCommissionError,
                        enabled = !autoCalculateChecked,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 5.dp)
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
                        label = { Text(text = stringResource(id = R.string.transaction_tax)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal // 显示带有小数点的数字键盘
                        ),
                        isError = isTransactionTaxError,
                        enabled = !autoCalculateChecked,
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
                        text = stringResource(id = R.string.transaction_type),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(110.dp)
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
                        text = stringResource(id = R.string.stock_type),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(110.dp)
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
                        text = stringResource(id = R.string.transaction_date),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(110.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHeader(
    onSaveStockRecord: () -> Unit,
    navController: NavHostController,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(id = R.string.add_record),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "返回"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onSaveStockRecord()
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "確認"
                )
            }
        }
    )
}

@Composable
fun StockSymbolDropdown(
    stockSymbols: List<StockSymbol>,
    selectedStockSymbol: StockSymbol?,
    navController: NavHostController,
    onStockSymbolSelected: (StockSymbol) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = selectedStockSymbol?.stockSymbol ?: "",
            onValueChange = {},
//            label = { Text("選擇股票代碼") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.clickable {
                        navController.navigate("stockSymbolScreen")
//                        expanded = true
                    }
                )
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = true
                            }
                        }
                    }
                },
            modifier = Modifier.padding(5.dp)
        )
        if (expanded) {
            Dialog(onDismissRequest = { expanded = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .height(400.dp)
                ) {
                    Column {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("搜尋股票代碼") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )
                        val filteredStockSymbols = stockSymbols.filter {
                            it.stockSymbol.contains(searchQuery, ignoreCase = true) ||
                                    it.stockName.contains(searchQuery, ignoreCase = true)
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
//                                .heightIn(max = 400.dp) // 限制下拉列表的最大高度
                        ) {
                            items(filteredStockSymbols) { stockSymbol ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onStockSymbolSelected(stockSymbol)
                                            expanded = false
                                        }
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "${stockSymbol.stockSymbol} - ${stockSymbol.stockName}",
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
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
fun combineDateAndTimeVersion2(selectedDate: Long?, selectedTime: TimePickerState): Long {
    // 如果日期或时间为空，则返回 null
    if (selectedDate == null) return System.currentTimeMillis()
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