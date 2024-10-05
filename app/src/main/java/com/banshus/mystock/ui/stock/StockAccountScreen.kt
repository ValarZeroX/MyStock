package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.getProfitColor
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.theme.StockGreen
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.viewmodels.BillingViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel

@Composable
fun StockAccountScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel,
    billingViewModel: BillingViewModel
) {
    Scaffold(
        topBar = {
            AccountHeader(navController)
        },
        bottomBar = {
            AdBanner(billingViewModel) // 将广告放在底部栏
        }
    ) { innerPadding ->
        StockMainScreen(
            innerPadding,
            navController,
            stockViewModel,
            stockAccountViewModel,
            stockRecordViewModel,
            stockSymbolViewModel
        )
    }
}

@Composable
fun StockMainScreen(
    innerPadding: PaddingValues,
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel,
) {
    val context = LocalContext.current
    val stockSymbols by stockSymbolViewModel.allStockSymbols.observeAsState(emptyList())
    //calculateTotalCostAndProfitForAllAccounts 需要
    stockSymbolViewModel.fetchAllStockSymbols()
    LaunchedEffect(stockSymbols) {
        stockRecordViewModel.setStockSymbols(stockSymbols)
    }
    val stockAccounts by stockAccountViewModel.stockAccounts.observeAsState(emptyList())
    val costAndProfitForAllAccounts by stockRecordViewModel.calculateTotalCostAndProfitForAllAccounts()
        .observeAsState(emptyMap())
//    val stockRecordsHoldings by stockRecordViewModel.getHoldingsAndTotalCostGroupedByAccount().observeAsState(initial = emptyMap())


//    val selectedAccountForStockList by stockViewModel.selectedAccountForStockList.observeAsState()
//    Log.d("costAndProfitForAllAccounts", "$costAndProfitForAllAccounts")
//    Log.d("stockRecordsHoldings", "$stockRecordsHoldings")
//    LaunchedEffect(costAndProfitForAllAccounts) {
//        costAndProfitForAllAccounts.let {
//            Log.d("TotalCostAndProfit", it.toString())
//        }
//    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(innerPadding)
    ) {
        items(stockAccounts) { stockAccount ->
//            val holdingsAndTotalCost = stockRecordsHoldings[stockAccount.accountId]
//            val realizedGainsAndLosses = stockRecordsRealizedGainsAndLosses[stockAccount.accountId]
//            val totalCostBasis = holdingsAndTotalCost?.second ?: 0.0
            val accountProfitData = costAndProfitForAllAccounts[stockAccount.accountId]?.totalProfit
                ?: 0.0
            val accountProfit = formatNumber(accountProfitData)
            val profitColor = getProfitColor(
                accountProfitData,
                StockRed,
                StockGreen,
                MaterialTheme.colorScheme.onSurface
            )
            val accountPercentData = costAndProfitForAllAccounts[stockAccount.accountId]?.totalProfitPercent ?: 0.0
            val accountPercent = "${formatNumber(accountPercentData)} %"
            val profitPercentColor = getProfitColor(
                accountPercentData,
                StockRed,
                StockGreen,
                MaterialTheme.colorScheme.onSurface
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        stockViewModel.updateSelectedAccountForStockList(stockAccount)
                        navController.navigate("stockListScreen")
                    },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stockAccount.account,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Gray1,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
                        ) {
                            Row(modifier = Modifier.padding(vertical = 3.dp),verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = SharedOptions.getOptionStockMarket(context)[stockAccount.stockMarket],
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Gray1,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
                        ) {
                            Row(modifier = Modifier.padding(vertical = 3.dp),verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.AttachMoney,
                                    contentDescription = "Localized description",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stockAccount.currency,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(id = R.string.account_value),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stringResource(id = R.string.investment_cost),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = formatNumber(
                                    costAndProfitForAllAccounts[stockAccount.accountId]?.totalPrice
                                        ?: 0.0
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatNumber(
                                    costAndProfitForAllAccounts[stockAccount.accountId]?.totalCostBasis
                                        ?: 0.0
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(id = R.string.unrealized_gain_loss),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stringResource(id = R.string.unrealized_gain_loss_percentage),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = accountProfit,
                                color = profitColor, modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = accountPercent,
                                color = profitPercentColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(id = R.string.account_overview),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("AddAccountScreen")
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Account"
                )
            }
        }
    )
}