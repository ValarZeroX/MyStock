package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.data.entities.StockSymbol
import com.banshus.mystock.repository.StockAccountRepository
import com.banshus.mystock.repository.StockRecordRepository
import com.banshus.mystock.repository.StockSymbolRepository
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModelFactory
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModelFactory
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModelFactory
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAddScreen(navController: NavHostController, stockViewModel: StockViewModel) {
    val context = LocalContext.current
    val stockAccountViewModel: StockAccountViewModel = viewModel(
        factory = StockAccountViewModelFactory(
            StockAccountRepository(AppDatabase.getDatabase(context).stockAccountDao())
        )
    )

    val stockRecordRepository = StockRecordRepository(AppDatabase.getDatabase(context).stockRecordDao())
    val stockRecordViewModel: StockRecordViewModel = viewModel(
        factory = StockRecordViewModelFactory(stockRecordRepository)
    )

    val stockSymbolViewModel: StockSymbolViewModel = viewModel(
        factory = StockSymbolViewModelFactory(
            StockSymbolRepository(AppDatabase.getDatabase(context).stockSymbolDao())
        )
    )

    //撈第一筆帳戶
    val firstStockAccount by stockAccountViewModel.firstStockAccount.observeAsState()


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
//    //股票代碼
//    var stockSymbol by remember { mutableStateOf("") }
//    // 股票名稱
//    var stockName by remember { mutableStateOf("") }
    //股票類型
    var selectedStockTypeIndex by remember { mutableIntStateOf(0) }
    //交易類別
    var selectedTransactionType by remember { mutableIntStateOf(0) }
    //交易時間
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00")
    val dateTimeString = "$stockDateTime ${selectedTime.value}"
    val localDateTime = LocalDateTime.parse(dateTimeString, dateTimeFormatter)
    val instant = localDateTime.toInstant(ZoneOffset.UTC)
    val transactionDate: Long = instant.toEpochMilli()
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
    var selectedDiscount by remember {mutableDoubleStateOf(0.0) }

    val decimalFormat = DecimalFormat("#.00")
    // Calculate commission based on quantity, price and commission percentage
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
    }

    val priceName = when(selectedTransactionType) {
        0 -> "每股價格"
        1 -> "每股價格"
        else -> "每股股利"
    }

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
                        text = "交易帳戶",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    if (firstStockAccount == null) {
                        Button(
                            onClick = {
                                navController.navigate("addAccountScreen")
                            }) {
                            Text(text = "新增帳戶")
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "新增帳戶"
                            )
                        }
                    } else {
                        val accountText: String
                        if (selectedAccount == null) {
                            accountText = firstStockAccount?.account ?: "No account selected"
                            selectedAccountId = firstStockAccount?.accountId ?: 0
                            selectedStockMarket= firstStockAccount?.stockMarket ?: 0
                            selectedAutoCalculate = firstStockAccount?.autoCalculate ?: false
                            selectedCommissionDecimal = firstStockAccount?.commissionDecimal ?: 0.0
                            selectedTransactionTax = firstStockAccount?.transactionTaxDecimal ?: 0.0
                            selectedDiscount = firstStockAccount?.discount ?: 1.0
                        } else {
                            accountText = selectedAccount?.account ?: "No account selected"
                            selectedAccountId = selectedAccount?.accountId ?: 0
                            selectedStockMarket= selectedAccount?.stockMarket ?: 0
                            selectedAutoCalculate = selectedAccount?.autoCalculate ?: false
                            selectedCommissionDecimal = selectedAccount?.commissionDecimal ?: 0.0
                            selectedTransactionTax = selectedAccount?.transactionTaxDecimal ?: 0.0
                            selectedDiscount = selectedAccount?.discount ?: 1.0
                        }
                        Button(
                            onClick = {
                                navController.navigate("accountListScreen")
                            }) {
                            Text(text = accountText)
                        }
                    }
                }
            }
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
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "股數",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
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
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isStockQuantityError,
                        modifier = Modifier
                            .padding(5.dp)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = priceName,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
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
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isStockPriceError,
                        modifier = Modifier
                            .padding(5.dp)
                    )
                }
            }
            item {
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
            }
            item {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "手續費",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
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
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isCommissionError,
                        enabled = !autoCalculateChecked,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "證交稅",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
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
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isTransactionTaxError,
                        enabled = !autoCalculateChecked,
                        modifier = Modifier
                            .padding(5.dp)
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
                    val options = listOf("一般", "ETF", "當沖")
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHeader(
    onSaveStockRecord: () -> Unit,
    navController: NavHostController
    ) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "新增記錄",
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

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
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
            filteredStockSymbols.forEach { stockSymbol ->
                DropdownMenuItem(
                    text = { Text("${stockSymbol.stockSymbol} - ${stockSymbol.stockName}") },
                    onClick = {
                        onStockSymbolSelected(stockSymbol)
                        expanded = false
                    }
                )
            }
        }
    }
}