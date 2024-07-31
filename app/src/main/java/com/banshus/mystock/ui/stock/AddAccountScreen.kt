package com.banshus.mystock.ui.stock

import android.icu.util.Currency
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController


@Composable
fun AddAccountScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AddAccountScreenHeader(navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            var accountName by remember { mutableStateOf("") }
            var showDialog by remember { mutableStateOf(false) }
            var selectedCurrency by remember { mutableStateOf("TWD") }


            OutlinedTextField(
                value = accountName,
                onValueChange = {
                    if (it.length <= 20) {
                        accountName = it
                    }
                },
                label = { Text("帳戶名稱") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            )

            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text(selectedCurrency)
            }
            if (showDialog) {
                CurrencyDialog(
                    initialSelectedCurrency = selectedCurrency,
//                    onCurrencySelected = { currency ->
//                        selectedCurrency = currency
//                        showDialog = false
//                    },
                    onDismissRequest = { showDialog = false },
                    onConfirmation = { showDialog = false },
                )
            }
        }
    }
}

@Composable
fun CurrencyDialog(
    initialSelectedCurrency: String,
//    onCurrencySelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    val currencies = listOf(
        "AED",  // United Arab Emirates Dirham
        "ARS",  // Argentine Peso
        "AUD",  // Australian Dollar
        "BRL",  // Brazilian Real
        "CAD",  // Canadian Dollar
        "CHF",  // Swiss Franc
        "CLP",  // Chilean Peso
        "CNY",  // Chinese Yuan
        "COP",  // Colombian Peso
        "CZK",  // Czech Koruna
        "DKK",  // Danish Krone
        "EUR",  // Euro
        "GBP",  // British Pound Sterling
        "HKD",  // Hong Kong Dollar
        "HUF",  // Hungarian Forint
        "IDR",  // Indonesian Rupiah
        "ILS",  // Israeli New Shekel
        "INR",  // Indian Rupee
        "JMD",  // Jamaican Dollar
        "JPY",  // Japanese Yen
        "KRW",  // South Korean Won
        "LKR",  // Sri Lankan Rupee
        "MAD",  // Moroccan Dirham
        "MXN",  // Mexican Peso
        "MYR",  // Malaysian Ringgit
        "NOK",  // Norwegian Krone
        "NZD",  // New Zealand Dollar
        "PEN",  // Peruvian Nuevo Sol
        "PHP",  // Philippine Peso
        "PLN",  // Polish Zloty
        "RUB",  // Russian Ruble
        "SAR",  // Saudi Riyal
        "SEK",  // Swedish Krona
        "SGD",  // Singapore Dollar
        "THB",  // Thai Baht
        "TRY",  // Turkish Lira
        "TWD",  // New Taiwan Dollar
        "ZAR"   // South African Rand
    )

    val listState = rememberLazyListState()
    var selectedCurrency by remember { mutableStateOf(initialSelectedCurrency) }
//    val selectedIndex = currencies.indexOf(selectedCurrency)
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            onClick = { /*TODO*/ }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "選擇幣別")
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(20.dp)
                        .height(120.dp)
                        .selectableGroup(),
                ) {
                    itemsIndexed(currencies) { index, currency ->
                        CurrencyItem(
                            selectedIndex = if (currency == selectedCurrency) index else -1,
                            index = index,
                            currency = currency,
                            onClick = {
                                selectedCurrency = currency
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("取消")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("確定")
                    }
                }
            }
        }
    }
}


@Composable
fun CurrencyItem(
    selectedIndex: Int,
    index: Int,
    currency: String,
    onClick: () -> Unit
) {
    val isSelected = selectedIndex == index
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    Color.Blue
                } else {
                    Color.Transparent
                }
            )
            .selectable(
                selected = isSelected,
                onClick = onClick
            )
//            .clickable { onClick(index) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = currency,
            color = if (isSelected) Color.White else Color.White,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreenHeader(navController: NavHostController) {
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
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "確定"
                )
            }
        }
    )
}
