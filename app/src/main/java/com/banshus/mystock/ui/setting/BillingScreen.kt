package com.banshus.mystock.ui.setting

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.viewmodels.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    navController: NavHostController,
    billingViewModel : BillingViewModel
) {
    val skuDetailsList by billingViewModel.skuDetailsList.observeAsState(emptyList()) // 觀察可用的訂閱方案
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "訂閱",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back Color Theme"
                        )
                    }
                },
            )
        },
        bottomBar = {
            AdBanner() // 将广告放在底部栏
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Text(text = if (skuDetailsList.contains("github_sub")) "Purchased" else "Not Purchased")
            if (skuDetailsList.isEmpty()) {
                // 如果訂閱產品列表為空，顯示一個提示
                Text(text = "沒有可用的訂閱方案", modifier = Modifier.padding(16.dp))
            } else {
                // 顯示可用的訂閱產品
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    TextButton(
                        onClick = {
                            billingViewModel.checkSubscriptionStatus("monthly")
                        }
                    ) {
                        Text(text = "Monthly plan")
                    }
                    TextButton(
                        onClick = {
                            billingViewModel.checkSubscriptionStatus("yearly")
                        }
                    ) {
                        Text(text = "Yearly plan")
                    }
                }
            }
        }
    }
}