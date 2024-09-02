package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.viewmodels.StockAccountViewModel

@Composable
fun AccountListScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel
) {
    val context = LocalContext.current
    val stockAccounts by stockAccountViewModel.stockAccounts.observeAsState(emptyList())

    Scaffold(
        topBar = {
            AccountListHeader(navController)
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
            LazyColumn(
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                items(stockAccounts) { stockAccount ->
                    val stockMarketName = SharedOptions.getOptionStockMarket(context)[stockAccount.stockMarket]
                    ListItem(
                        headlineContent = { Text(text = stockAccount.account) },
                        supportingContent = {
                            Column {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = stringResource(id = R.string.commission) , Modifier.weight(1f))
                                    Text(text = stringResource(id = R.string.transaction_tax), Modifier.weight(1f))
                                    Text(text = stringResource(id = R.string.commission_discount), Modifier.weight(1f))
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "${stockAccount.commissionDecimal}",
                                        Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${stockAccount.transactionTaxDecimal}",
                                        Modifier.weight(1f)
                                    )
                                    Text(text = "${stockAccount.discount}", Modifier.weight(1f))
                                }

                            }
                        },
                        trailingContent = { Text(text = stockMarketName) },
                        modifier = Modifier.clickable {
                            stockViewModel.updateSelectedAccount(stockAccount)
                            navController.popBackStack()
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(id = R.string.select_account),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("addAccountScreen")
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    )
}