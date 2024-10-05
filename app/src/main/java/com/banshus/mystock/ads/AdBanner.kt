package com.banshus.mystock.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.banshus.mystock.viewmodels.BillingViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(billingViewModel: BillingViewModel) {
    val context = LocalContext.current
    val subscriptions by billingViewModel.skuDetailsList.observeAsState(emptyList())
    if (!subscriptions.contains("stock_tracker_monthly")) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = {
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-9868429025931364/8633003875"  // 替换为你的广告单元 ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}