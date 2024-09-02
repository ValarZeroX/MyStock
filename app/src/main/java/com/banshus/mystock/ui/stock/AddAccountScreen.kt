package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.viewmodels.CurrencyApiViewModel
import com.banshus.mystock.viewmodels.CurrencyViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModel
import java.math.BigDecimal
import java.math.RoundingMode

data class Currency(val code: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    navController: NavHostController,
    stockAccountViewModel: StockAccountViewModel,
    currencyViewModel: CurrencyViewModel,
    currencyApiViewModel: CurrencyApiViewModel,
) {
    val context = LocalContext.current

    var accountName by remember { mutableStateOf("") }
    var selectedStockMarketIndex by remember { mutableIntStateOf(0) }
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

    val currencyRates by currencyApiViewModel.currencyRates.observeAsState()
    LaunchedEffect(Unit) {
        currencyApiViewModel.fetchCurrencyRates()
    }
    Scaffold(
        topBar = {
            AddAccountScreenHeader(
                navController,
                onSave = {
                    // 在保存時，將帳戶名稱和所選幣別儲存到數據庫
                    val currencyCode = when (selectedStockMarketIndex) {
                        0 -> "TWD"
                        1 -> "USD"
                        else -> "TWD"
                    }

                    var commission = roundToDecimal(commissionPercent.toDouble() / 100, 6)
                    var transactionTax = roundToDecimal(transactionTaxPercent.toDouble() / 100, 6)
                    var newDiscount = roundToDecimal(discount.toDouble() / 100, 4)
                    if (selectedStockMarketIndex != 0) {
                        commission = 0.0
                        transactionTax = 0.0
                        newDiscount = 0.0
                    }
                    stockAccountViewModel.insertStockAccount(
                        accountName,
                        currencyCode,
                        selectedStockMarketIndex,
                        checked,
                        commission,
                        transactionTax,
                        newDiscount
                    )
                    var currencyKey = "USD"
                    if (currencyCode != "USD"){
                         currencyKey = "USD$currencyCode"
                    }

                    val currencyRate = currencyRates?.get(currencyKey)

                    val currentTime = System.currentTimeMillis()
                    val currency = currencyRate?.let {
                        com.banshus.mystock.data.entities.Currency(
                            currencyCode = currencyCode,
                            exchangeRate = it.exchangeRate,
                            lastUpdatedTime = currentTime
                        )
                    }
                    if (currency != null) {
                        currencyViewModel.insertCurrency(
                            currency,
                        )
                    }
                    navController.popBackStack() // 儲存完成後返回上一頁
                }
            )
        },
        bottomBar = {
            AdBanner() // 将广告放在底部栏
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {

            Row(modifier = Modifier.padding(15.dp)){
                Text(
                    text = stringResource(id = R.string.account_name),
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
                    text = stringResource(id = R.string.stock_market),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(100.dp)
                        .padding(start = 10.dp, end = 20.dp),
                )
                val options = SharedOptions.getOptionStockMarket(context)
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
                        text = stringResource(id = R.string.advanced_settings),
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
                        text = stringResource(id = R.string.commission),
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
                        text = stringResource(id = R.string.transaction_tax),
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
                        text = stringResource(id = R.string.commission_discount),
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
                stringResource(id = R.string.add_account),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onSave()
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Check"
                )
            }
        }
    )
}

fun roundToDecimal(value: Double, decimalPlaces: Int): Double {
    val bd = BigDecimal(value)
    return bd.setScale(decimalPlaces, RoundingMode.HALF_UP).toDouble()
}