package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.StockAccountRepository
import com.banshus.mystock.repository.StockMarketRepository
import com.banshus.mystock.repository.StockSymbolRepository
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModelFactory
import com.banshus.mystock.viewmodels.StockMarketViewModel
import com.banshus.mystock.viewmodels.StockMarketViewModelFactory
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModelFactory
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class Currency(val code: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(navController: NavHostController,stockAccountViewModel: StockAccountViewModel) {

//    val context = LocalContext.current
//    val stockAccountViewModel: StockAccountViewModel = viewModel(
//        factory = StockAccountViewModelFactory(
//            StockAccountRepository(AppDatabase.getDatabase(context).stockAccountDao())
//        )
//    )

//    val stockMarketViewModel: StockMarketViewModel = viewModel(
//        factory = StockMarketViewModelFactory(
//            StockMarketRepository(AppDatabase.getDatabase(context).stockMarketDao())
//        )
//    )
//    val repository = StockAccountRepository(AppDatabase.getDatabase(context).stockAccountDao())
//    val factory = StockAccountViewModelFactory(repository)
//    val stockAccountViewModel: StockAccountViewModel = viewModel(
//        factory = factory
//    )

    var accountName by remember { mutableStateOf("") }
//    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }
    var selectedStockMarketIndex by remember { mutableIntStateOf(0) }
    var selectedBrokerageIndex by remember { mutableIntStateOf(0) }
    var checked by remember { mutableStateOf(false) }
    // 手續費
    var commissionPercent by remember { mutableStateOf("0.1425") }
    var isCommissionError by remember { mutableStateOf(false) }
    // 證交稅
    var transactionTaxPercent by remember { mutableStateOf("0.3") }
    var isTransactionTaxError by remember { mutableStateOf(false) }
    // 手續費折扣
    var discount by remember { mutableStateOf("100") }
    var isDiscountError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AddAccountScreenHeader(
                navController,
                onSave = {
                    // 在保存時，將帳戶名稱和所選幣別儲存到數據庫
//                    val currencyCode = selectedCurrency?.code ?: ""
                    val currencyCode = when (selectedStockMarketIndex) {
                        0 -> "TWD"
                        1 -> "USD"
                        else -> "TWD"
                    }

                    val commission = roundToDecimal(commissionPercent.toDouble() / 100, 6)
                    val transactionTax = roundToDecimal(transactionTaxPercent.toDouble() / 100, 6)
                    val newDiscount = roundToDecimal(discount.toDouble() / 100, 4)

                    stockAccountViewModel.insertStockAccount(
                        accountName,
                        currencyCode,
                        selectedStockMarketIndex,
                        checked,
                        commission,
                        transactionTax,
                        newDiscount
                    )
                    navController.popBackStack() // 儲存完成後返回上一頁
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {

//            val currencies = listOf(
//                Currency("TWD", "新台幣"),
//                Currency("USD", "美金"),
//                // 添加更多幣別
//            )
//            // 設定預設的貨幣為 USD
//            LaunchedEffect(currencies) {
//                selectedCurrency = currencies.find { it.code == "TWD" }
//            }
            Row(modifier = Modifier.padding(15.dp)){
                Text(
                    text = "帳戶名稱",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(100.dp)
                        .padding(start = 10.dp, end = 20.dp),
                )
                OutlinedTextField(
                    value = accountName,
                    onValueChange = {
                        if (it.length <= 20) {
                            accountName = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                )
            }

            Row(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = "股票市場",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(100.dp)
                        .padding(start = 10.dp, end = 20.dp),
                )
                val options = listOf("台股", "美股")
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                            onClick = { selectedStockMarketIndex = index },
                            selected = index == selectedStockMarketIndex,
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }

            if (selectedStockMarketIndex == 0) {
                Row(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Text(
                        text = "進階設定",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )

                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                        }
                    )
                }
            }
            if (checked && selectedStockMarketIndex == 0) {
                Row(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "手續費",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    OutlinedTextField(
                        value = commissionPercent,
                        onValueChange = { newText ->
                            // 驗證是否正浮點數
                            val parsedValue = newText.toFloatOrNull()
                            if (newText.isEmpty() || (parsedValue != null && parsedValue in 0f..100f)) {
                                commissionPercent = newText
                                commissionPercent = newText
                                isCommissionError = false
                            } else {
                                isCommissionError = true
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isCommissionError,
                        suffix = {
                            Text(text = "%")
                        }
                    )
                }
                Row(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "證交稅",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    OutlinedTextField(
                        value = transactionTaxPercent,
                        onValueChange = { newText ->
                            // 驗證是否正浮點數
                            val parsedValue = newText.toFloatOrNull()
                            if (newText.isEmpty() || (parsedValue != null && parsedValue in 0f..100f)) {
                                transactionTaxPercent = newText
                                transactionTaxPercent = newText
                                isTransactionTaxError = false
                            } else {
                                isTransactionTaxError = true
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isTransactionTaxError,
                        suffix = {
                            Text(text = "%")
                        }
                    )
                }
                Row(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "手續費折扣",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(100.dp)
                            .padding(start = 10.dp, end = 20.dp),
                    )
                    OutlinedTextField(
                        value = discount,
                        onValueChange = { newText ->
                            // 驗證是否正浮點數
                            val parsedValue = newText.toFloatOrNull()
                            if (newText.isEmpty() || (parsedValue != null && parsedValue in 0f..100f)) {
                                discount = newText
                                discount = newText
                                isDiscountError = false
                            } else {
                                isDiscountError = true
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isDiscountError,
                        suffix = {
                            Text(text = "%")
                        }
                    )
                }
            }

//            if (selectedStockMarketIndex == 1 && checked) {
//                Row(
//                    modifier = Modifier.padding(15.dp)
//                ) {
//                    Text(
//                        text = "股票券商",
//                        modifier = Modifier
//                            .align(Alignment.CenterVertically)
//                            .width(100.dp)
//                            .padding(start = 10.dp, end = 20.dp),
//                    )
//                    val options = listOf("複委託", "海外券商")
//                    SingleChoiceSegmentedButtonRow(
//                        modifier = Modifier.fillMaxWidth(),
//                    ) {
//                        options.forEachIndexed { index, label ->
//                            SegmentedButton(
//                                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
//                                onClick = { selectedBrokerageIndex = index },
//                                selected = index == selectedBrokerageIndex,
//                            ) {
//                                Text(
//                                    text = label,
//                                    modifier = Modifier.padding(5.dp)
//                                )
//                            }
//                        }
//                    }
//                }
//            }


//            CurrencyDropdown(
//                currencies = currencies,
//                selectedCurrency = selectedCurrency,
//                onCurrencySelected = { selectedCurrency = it }
//            )
        }
    }
}

@Composable
fun CurrencyDropdown(
    currencies: List<Currency>,
    selectedCurrency: Currency?,
    onCurrencySelected: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = selectedCurrency?.code ?: "",
            onValueChange = {},
            label = { Text("選擇幣別") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.clickable { expanded = true }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
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
                label = { Text("搜尋幣別") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            )
            val filteredCurrencies = currencies.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true)
            }
            filteredCurrencies.forEach { currency ->
                DropdownMenuItem(
                    text = {Text("${currency.code} - ${currency.name}")},
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreenHeader(
    navController: NavHostController,
    onSave: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "新增帳戶",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "返回"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onSave()
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "確定"
                )
            }
        }
    )
}

fun roundToDecimal(value: Double, decimalPlaces: Int): Double {
    val bd = BigDecimal(value)
    return bd.setScale(decimalPlaces, RoundingMode.HALF_UP).toDouble()
}