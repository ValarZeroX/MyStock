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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.banshus.mystock.viewmodels.CurrencyApiViewModel
import com.banshus.mystock.viewmodels.CurrencyViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModel

@Composable
fun CurrencyScreen(
    navController: NavHostController,
    userSettingsViewModel: UserSettingsViewModel,
    currencyViewModel: CurrencyViewModel,
    currencyApiViewModel: CurrencyApiViewModel
){
    Scaffold(
        topBar = {
            CurrencyScreenHeader(
                navController,
            )
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
                            text = "主幣別",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(150.dp)
                                .padding(start = 10.dp, end = 20.dp),
                        )
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
                "幣別匯率",
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