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
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.SharedOptions.optionStockMarket
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.StockAccountRepository
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockAccountViewModelFactory

@Composable
fun AccountListScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel
) {
//    val context = LocalContext.current
//    val stockAccountViewModel: StockAccountViewModel = viewModel(
//        factory = StockAccountViewModelFactory(
//            StockAccountRepository(AppDatabase.getDatabase(context).stockAccountDao())
//        )
//    )
//    val repository = StockAccountRepository(AppDatabase.getDatabase(context).stockAccountDao())
//    val factory = StockAccountViewModelFactory(repository)
//    val stockAccountViewModel: StockAccountViewModel = viewModel(
//        factory = factory
//    )

    val stockAccounts by stockAccountViewModel.stockAccounts.observeAsState(emptyList())

//    val stockViewModel: StockViewModel = viewModel()
//    val selectedAccount by remember { mutableStateOf(viewModel.selectedAccount) }
//    val selectedAccount = remember {
//
//    }
//    val viewModel: StockViewModel by viewModels()
//    val viewModel by viewModels<StockViewModel>()
//    private lateinit var viewModel: StockViewModel
//    println(viewModel.selectedAccount)
    Scaffold(
        topBar = {
            AccountListHeader(navController)
        },
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
                    val stockMarketName = when (stockAccount.stockMarket) {
                        0 -> stringResource(R.string.taiwan_stocks)
                        1 -> stringResource(R.string.us_stocks)
                        else -> stringResource(R.string.taiwan_stocks) // 默认值，可以根据需要调整
                    }
                    ListItem(
                        headlineContent = { Text(text = stockAccount.account) },
                        supportingContent = {
                            Column {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "市場", Modifier.weight(1f))
                                    Text(text = "幣別", Modifier.weight(1f))
                                    Text(text = "手續費優惠", Modifier.weight(1f))
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = optionStockMarket[stockAccount.stockMarket],
                                        Modifier.weight(1f)
                                    )
                                    Text(text = stockAccount.currency, Modifier.weight(1f))
                                    Text(text = "${stockAccount.discount}", Modifier.weight(1f))
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "手續費", Modifier.weight(1f))
                                    Text(text = "證交稅", Modifier.weight(1f))
                                    Text(text = "手續費優惠", Modifier.weight(1f))
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
                        leadingContent = {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Localized description",
                            )
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
                "帳戶列表",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "關閉"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("addAccountScreen")
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "新增"
                )
            }
        }
    )
}