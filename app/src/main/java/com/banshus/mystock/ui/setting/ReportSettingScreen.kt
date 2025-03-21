package com.banshus.mystock.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.viewmodels.BillingViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModel

@Composable
fun ReportSettingScreen(
    navController: NavHostController,
    userSettingsViewModel: UserSettingsViewModel,
    billingViewModel: BillingViewModel
) {
    var calculateCommission by remember { mutableStateOf(false) }
    var calculateTransactionTax by remember { mutableStateOf(false) }
    var calculateDividend by remember { mutableStateOf(false) }

    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    LaunchedEffect(userSettings) {
        calculateCommission = userSettings!!.isCommissionCalculationEnabled
        calculateTransactionTax = userSettings!!.isTransactionTaxCalculationEnabled
        calculateDividend = userSettings!!.isDividendCalculationEnabled
    }
    Scaffold(
        topBar = {
            ReportSettingScreenHeader(
                navController,
            )
        },
        bottomBar = {
            AdBanner(billingViewModel) // 将广告放在底部栏
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
                            text = stringResource(id = R.string.commission_calculation),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(150.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        Switch(
                            checked = calculateCommission,
                            onCheckedChange = {
                                calculateCommission = it
                                userSettingsViewModel.updateReportSettings(
                                    calculateCommission,
                                    calculateTransactionTax,
                                    calculateDividend
                                )
                            },
                            modifier = Modifier
                                .semantics {
                                    contentDescription = if (calculateDividend) {
                                        "Calculate Comm. On"  // 狀態為開啟
                                    } else {
                                        "Calculate Comm. Off"  // 狀態為關閉
                                    }
                                }
                        )
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                        Text(
                            text = stringResource(id = R.string.profit_loss_includes_commission),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, end = 20.dp),
                            fontSize = 14.sp,
                            color = Gray1
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.transaction_tax_calculation),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(150.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        Switch(
                            checked = calculateTransactionTax,
                            onCheckedChange = {
                                calculateTransactionTax = it
                                userSettingsViewModel.updateReportSettings(
                                    calculateCommission,
                                    calculateTransactionTax,
                                    calculateDividend
                                )
                            },
                            modifier = Modifier
                                .semantics {
                                    contentDescription = if (calculateDividend) {
                                        "Calculate Tax On"  // 狀態為開啟
                                    } else {
                                        "Calculate Tax Off"  // 狀態為關閉
                                    }
                                }
                        )
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                        Text(
                            text = stringResource(id = R.string.profit_loss_includes_transaction_tax),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, end = 20.dp),
                            fontSize = 14.sp,
                            color = Gray1
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.dividend_calculation),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(150.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        Switch(
                            checked = calculateDividend,
                            onCheckedChange = {
                                calculateDividend = it
                                userSettingsViewModel.updateReportSettings(
                                    calculateCommission,
                                    calculateTransactionTax,
                                    calculateDividend
                                )
                            },
                            modifier = Modifier
                                .semantics {
                                    contentDescription = if (calculateDividend) {
                                        "Calculate Dividend On"  // 狀態為開啟
                                    } else {
                                        "Calculate Dividend Off"  // 狀態為關閉
                                    }
                                }
                        )
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                        Text(
                            text = stringResource(id = R.string.profit_loss_includes_dividends),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, end = 20.dp),
                            fontSize = 14.sp,
                            color = Gray1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSettingScreenHeader(
    navController: NavHostController,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(id = R.string.report_settings),
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