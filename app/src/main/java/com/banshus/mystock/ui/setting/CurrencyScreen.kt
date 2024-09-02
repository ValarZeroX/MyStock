package com.banshus.mystock.ui.setting

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import com.banshus.mystock.CurrencyUtils
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.api.response.CurrencyRate
import com.banshus.mystock.data.entities.Currency
import com.banshus.mystock.viewmodels.CurrencyApiViewModel
import com.banshus.mystock.viewmodels.CurrencyViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun CurrencyScreen(
    navController: NavHostController,
    userSettingsViewModel: UserSettingsViewModel,
    currencyViewModel: CurrencyViewModel,
    currencyApiViewModel: CurrencyApiViewModel
) {
    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    var selectedCurrencyCode by remember { mutableStateOf(userSettings?.currency ?: "") }
    val allCurrencies by currencyViewModel.allCurrencies.observeAsState()
    val currencyRates by currencyApiViewModel.currencyRates.observeAsState()
    LaunchedEffect(Unit) {
        currencyApiViewModel.fetchCurrencyRates()
    }
    LaunchedEffect(selectedCurrencyCode) {
        if (selectedCurrencyCode.isNotEmpty()) {
            updateCurrenciesWithNewBase(
                selectedCurrencyCode = selectedCurrencyCode,
                currencyRates = currencyRates ?: emptyMap(),
                allCurrencies = allCurrencies ?: emptyList(),
                currencyViewModel = currencyViewModel
            )
        }
    }

    Scaffold(
        topBar = {
            CurrencyScreenHeader(
                navController,
            )
        },
        bottomBar = {
            AdBanner() // 将广告放在底部栏
        }
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
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.primary_currency),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(150.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        CurrencyDropdown(
                            selectedCurrencyCode = selectedCurrencyCode,
                            onCurrencySelected = { newCurrencyCode ->
                                selectedCurrencyCode = newCurrencyCode
                                userSettingsViewModel.updateCurrencyCode(newCurrencyCode)
                            }
                        )
                    }
                }
                items(allCurrencies ?: emptyList()) { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(text = currency.currencyCode)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = currency.exchangeRate.toString())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreenHeader(
    navController: NavHostController,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(id = R.string.currency_rate),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Composable
fun CurrencyDropdown(
    selectedCurrencyCode: String?,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val currencyList = SharedOptions.currencyCodes.map {
        it to CurrencyUtils.getCurrencyName(context, it)
    }

    Column {
            OutlinedTextField(
                value = selectedCurrencyCode ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow",
                        modifier = Modifier.clickable {
                            expanded = true
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
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
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
                        // 搜索框
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text(stringResource(id = R.string.search_currency)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )
                        // 下拉列表
                        val filteredCurrencies = currencyList.filter {
                            it.first.contains(searchQuery, ignoreCase = true) ||
                                    it.second.contains(searchQuery, ignoreCase = true)
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
//                                .heightIn(max = 400.dp) // 限制下拉列表的最大高度
                        ) {
                            items(filteredCurrencies) { (currencyCode, currencyName) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onCurrencySelected(currencyCode)
                                            expanded = false
                                        }
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "$currencyCode - $currencyName",
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

private fun updateCurrenciesWithNewBase(
    selectedCurrencyCode: String,
    currencyRates: Map<String, CurrencyRate>,
    allCurrencies: List<Currency>,
    currencyViewModel: CurrencyViewModel
) {
    val usdToNewBaseRate = currencyRates["USD$selectedCurrencyCode"]?.exchangeRate

    if (usdToNewBaseRate != null) {
        for (currency in allCurrencies) {
            val usdToCurrencyRate = currencyRates["USD${currency.currencyCode}"]?.exchangeRate
            if (usdToCurrencyRate != null) {
                val newExchangeRate = roundToDecimal(usdToCurrencyRate / usdToNewBaseRate, 6)
                val updatedCurrency = currency.copy(
                    exchangeRate = newExchangeRate,
                    lastUpdatedTime = System.currentTimeMillis()
                )
                currencyViewModel.updateCurrency(updatedCurrency)
            }
        }
    }
}

fun roundToDecimal(value: Double, decimalPlaces: Int): Double {
    return BigDecimal(value).setScale(decimalPlaces, RoundingMode.HALF_UP).toDouble()
}