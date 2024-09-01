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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.viewmodels.UserSettingsViewModel

@Composable
fun AutoUpdateScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    userSettingsViewModel: UserSettingsViewModel
) {
    var autoUpdateStockPrice by remember { mutableStateOf(false) }
    var autoUpdateExchangeRate by remember { mutableStateOf(false) }
    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    LaunchedEffect(userSettings) {
        autoUpdateStockPrice = userSettings!!.autoUpdateStock
        autoUpdateExchangeRate = userSettings!!.autoUpdateExchangeRate
    }
    Scaffold(
        topBar = {
            AutoUpdateScreenHeader(navController)
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
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = "股價",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(150.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        Switch(
                            checked = autoUpdateStockPrice,
                            onCheckedChange = {
                                autoUpdateStockPrice = it
                                userSettingsViewModel.updateAutoStockPrice(
                                    autoUpdateStockPrice,
                                )
                            }
                        )
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                        Text(
                            text = "自動更新股價開關，每15分鐘更新股價。關閉可到股票代碼功能手動更新。",
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
                            text = "匯率",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(150.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
                        Switch(
                            checked = autoUpdateExchangeRate,
                            onCheckedChange = {
                                autoUpdateExchangeRate = it
                                userSettingsViewModel.updateAutoExchangeRate(
                                    autoUpdateExchangeRate,
                                )
                            }
                        )
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                        Text(
                            text = "自動更新匯率，每15分鐘更新匯率。",
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
fun AutoUpdateScreenHeader(
    navController: NavHostController,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "自動更新設定",
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
    )
}