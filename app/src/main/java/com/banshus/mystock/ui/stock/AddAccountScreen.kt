package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.StockAccountRepository
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModelFactory

data class Currency(val code: String, val name: String)

@Composable
fun AddAccountScreen(navController: NavHostController) {

    val context = LocalContext.current
    val repository = StockAccountRepository(AppDatabase.getDatabase(context).stockAccountDao())
    val factory = StockAccountViewModelFactory(repository)
    val stockAccountViewModel: StockAccountViewModel = viewModel(
        factory = factory
    )

    var accountName by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }

    Scaffold(
        topBar = {
            AddAccountScreenHeader(
                navController,
                onSave = {
                    // 在保存時，將帳戶名稱和所選幣別儲存到數據庫
                    val currencyCode = selectedCurrency?.code ?: ""
                    stockAccountViewModel.insertStockAccount(accountName, currencyCode)
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

            val currencies = listOf(
                Currency("TWD", "新台幣"),
                Currency("USD", "美金"),
                Currency("JPY", "日圓"),
                // 添加更多幣別
            )
            // 設定預設的貨幣為 USD
            LaunchedEffect(currencies) {
                selectedCurrency = currencies.find { it.code == "USD" }
            }

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

            CurrencyDropdown(
                currencies = currencies,
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { selectedCurrency = it }
            )
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
