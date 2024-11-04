package com.banshus.mystock.ui.setting

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.viewmodels.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    navController: NavHostController,
    billingViewModel: BillingViewModel
) {
    val context = LocalContext.current


    val subscriptions by billingViewModel.skuDetailsList.observeAsState(emptyList())
    // 觀察產品詳細信息
    val productDetailsList by billingViewModel.productDetailsList.observeAsState(emptyList())

// 查詢產品列表
    LaunchedEffect(Unit) {
        billingViewModel.querySubscriptionPlans(listOf("stock_tracker_monthly")) // 輸入您的產品ID"stock_tracker_monthly",
    }
    Log.d("productDetailsList", "$productDetailsList")
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.subscription),
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
            AdBanner(billingViewModel) // 将广告放在底部栏
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 專業版標題
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween // 讓內容平鋪在兩邊
                        ) {
                            Text(
                                text = stringResource(id = R.string.pro_version),
                                fontSize = 20.sp, // 標題較大字體
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp)) // 添加一些間距

                        // 移除廣告文字加圖標
                        Row(
                            verticalAlignment = Alignment.CenterVertically // 讓文字與圖標對齊
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Check Icon",
                                tint = Color.Green, // 設置綠色的勾選圖標
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // 圖標與文字間的間距
                            Text(
                                text = stringResource(id = R.string.remove_ads),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                if (subscriptions.contains("stock_tracker_monthly")) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        Button(
                            onClick = { /* Handle click action */ },
                            enabled = false
                        ) {
                            Text(
                                text = stringResource(id = R.string.subscribed),
                                color = Gray1
                            )
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (productDetailsList.isNotEmpty()) {
                            // 遍歷產品列表，顯示產品的標題和價格
                            productDetailsList.forEach { productDetails ->
                                productDetails.subscriptionOfferDetails?.forEach { offer ->
                                    val basePlanId = offer.basePlanId // 獲取基礎方案ID
                                    val pricingPhase =
                                        offer.pricingPhases.pricingPhaseList[0] // 獲取第一個定價階段
                                    val formattedPrice = pricingPhase?.formattedPrice ?: "N/A"
                                    val billingPeriod = pricingPhase?.billingPeriod ?: "N/A"
                                    Button(
                                        onClick = {
                                            billingViewModel.purchaseSubscription(
                                                basePlanId,
                                                offer.offerToken
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp)
                                    ) {
                                        var str = stringResource(id = R.string.month)
                                        if (billingPeriod == "P1Y") {
                                            str = stringResource(id = R.string.year)
                                        }
                                        Text(text = "$formattedPrice / $str")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}