package com.banshus.mystock.ui.report

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.viewmodels.StockMarketViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel

@Composable
fun AllMarketScreen(
    navController: NavHostController,
    stockRecordViewModel: StockRecordViewModel,
    stockViewModel: StockViewModel,
    stockMarketViewModel: StockMarketViewModel,
    stockAccounts: Map<Int, StockAccount>,
) {

}