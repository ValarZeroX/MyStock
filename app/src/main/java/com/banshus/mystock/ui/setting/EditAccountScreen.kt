package com.banshus.mystock.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.data.entities.StockRecord
import com.banshus.mystock.ui.stock.roundToDecimal
import com.banshus.mystock.viewmodels.StockAccountViewModel
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
) {
    val selectedAccount by stockViewModel.selectedAccount.observeAsState()
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

    LaunchedEffect(selectedAccount) {
        accountName = selectedAccount!!.account
        selectedStockMarketIndex = selectedAccount!!.stockMarket
        commissionPercent = (selectedAccount!!.commissionDecimal * 100)
            .toBigDecimal()
            .setScale(8, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toString()
        transactionTaxPercent = (selectedAccount!!.transactionTaxDecimal * 100)
            .toBigDecimal()
            .setScale(8, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toString()
        discount = (selectedAccount!!.discount * 100)
            .toBigDecimal()
            .setScale(8, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }
    Scaffold(
        topBar = {
            EditAccountScreenHeader(
                navController,
                selectedAccount!!,
                onSaveStockAccount = {
                    val commission = roundToDecimal(commissionPercent.toDouble() / 100, 6)
                    val transactionTax = roundToDecimal(transactionTaxPercent.toDouble() / 100, 6)
                    val newDiscount = roundToDecimal(discount.toDouble() / 100, 4)
                    val stockAccount = StockAccount(
                        accountId = selectedAccount!!.accountId,
                        account =  accountName,
                        currency = selectedAccount!!.currency,
                        stockMarket = selectedStockMarketIndex,
                        autoCalculate = false,
                        commissionDecimal = commission,
                        transactionTaxDecimal = transactionTax,
                        discount = newDiscount,
                        accountSort = 0,
                        transactionTaxDecimalETF = 0.001,
                        transactionTaxDecimalDayTrading = 0.0015,
                        commissionWholeLot = 0.0,
                        commissionOddLot = 0.0,
                    )
                    stockAccountViewModel.updateStockAccount(stockAccount)
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
            Row(modifier = Modifier.padding(15.dp)) {
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
                            selectedAccount!!.account = it
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
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreenHeader(
    navController: NavHostController,
    selectedAccount: StockAccount,
    onSaveStockAccount: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "編輯 ${selectedAccount.account}",
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
                onSaveStockAccount()
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "更新"
                )
            }
        }
    )
}