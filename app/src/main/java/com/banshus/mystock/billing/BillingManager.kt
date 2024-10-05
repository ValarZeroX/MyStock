package com.banshus.mystock.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BillingManager(
    private val activity: Activity
) {
    private val _subscriptions = MutableStateFlow<List<String>>(emptyList())
    val subscriptions = _subscriptions.asStateFlow()

    // 保存所有的產品詳細信息
    private val _productDetailsList = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetailsList = _productDetailsList.asStateFlow()

    private val purchaseUpdateListener = PurchasesUpdatedListener { result, purchases ->
        Log.d("result", "$result")
        if (result.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (result.responseCode == BillingResponseCode.USER_CANCELED) {
            Log.d("BillingManager", "User canceled the purchase")
        } else {
            Log.d("BillingManager", "Purchase failed with response code: ${result}")
            // 其他錯誤處理邏輯
        }
    }

    private var billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(purchaseUpdateListener)
        .enablePendingPurchases()
        .build()

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                Log.d("billingResult", "$billingResult")
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // 更新訂閱狀態
                    _subscriptions.update {
                        val newList = it.toMutableList()
                        newList.addAll(purchase.products)
                        newList
                    }
                    Log.d("BillingManager", "Purchase acknowledged.")
                }
            }
        }
    }

    fun billingSetup() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingResponseCode.OK) {
                    Log.d("BillingManager result", "$result")
//                // 只有连接成功后，才允许启动订阅
                    hasSubscription()
                } else {
                    Log.d("BillingManager", "Billing setup failed with response code: ${result.responseCode}")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Handle billing service disconnection
                Log.e("BillingManager", "Billing service disconnected")
            }
        })
    }

    fun hasSubscription() {
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(
            queryPurchaseParams
        ) { result, purchases ->
            when (result.responseCode) {
                BillingResponseCode.OK -> {
                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            // User has an active subscription
                            _subscriptions.update {
                                val newList = it.toMutableList()
                                newList.addAll(purchase.products)
                                newList
                            }
                            return@queryPurchasesAsync
                        }
                    }
                }

                BillingResponseCode.USER_CANCELED -> {
                    // User canceled the purchase
                }

                else -> {
                    Log.e("error", "error $result")
                    // Handle other error cases
                }
            }
            Log.e("active", "active $result")
            // User does not have an active subscription

        }
    }

    fun checkSubscriptionStatus(
        subscriptionPlanId: String,
    ) {
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(
            queryPurchaseParams
        ) { result, purchases ->
            Log.d("BillingManager", "result details: ${result}")
            when (result.responseCode) {
                BillingResponseCode.OK -> {
                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.products.contains(
                                subscriptionPlanId
                            )
                        ) {
                            _subscriptions.update {
                                val newList = it.toMutableList()
                                newList.addAll(purchase.products)
                                newList
                            }
                            return@queryPurchasesAsync
                        }
                    }
                }

                BillingResponseCode.USER_CANCELED -> {
                    // User canceled the purchase
                }

                else -> {
                    // Handle other error cases
                }
            }
            // User does not have an active subscription
            querySubscriptionPlans(subscriptionPlanId)
        }
    }

    // 查詢訂閱計劃並存儲產品詳細信息
    private fun querySubscriptionPlans(
        subscriptionPlanId: String,
    ) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("stock_tracker_monthly")
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build(),
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                        Log.d("BillingManager", "billingResult details: $billingResult")
            Log.d("BillingManager", "productDetailsList details: $productDetailsList")
            if (billingResult.responseCode == BillingResponseCode.OK) {
                var offerToken = ""
                val productDetails = productDetailsList.firstOrNull { productDetails ->
                    productDetails.subscriptionOfferDetails?.any {
                        if (it.basePlanId == subscriptionPlanId) {
                            offerToken = it.offerToken
                            true
                        } else {
                            false
                        }
                    } == true
                }
                Log.d("BillingManager", "productDetails details: $productDetails")
                productDetails?.let {
                    val productDetailsParamsList = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(it)
                            .setOfferToken(offerToken)
                            .build()
                    )

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()

                    billingClient.launchBillingFlow(activity, billingFlowParams)
                }
            }
        }
    }

    // 查詢所有可供購買的訂閱方案
    fun querySubscriptionPlans(subscriptionPlanIds: List<String>) {
        val products = subscriptionPlanIds.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d("BillingManager", "Query successful: $productDetailsList")
                _productDetailsList.update { productDetailsList } // 保存查詢結果
            } else {
                Log.d("BillingManager", "Failed to query product details: $billingResult")
            }
        }
    }

    // ... (其他程式碼)

    fun purchaseSubscription(subscriptionPlanId: String, offerTokenBa: String) {
//        val productDetails = _productDetailsList.value.firstOrNull { it.productId == productId }
        Log.d("--------------","-------------------------------------------------")
        var offerToken = ""
        val productDetails = productDetailsList.value.firstOrNull { productDetails ->
            productDetails.subscriptionOfferDetails?.any {
                if (it.basePlanId == subscriptionPlanId) {
                    offerToken = it.offerToken
                    true
                } else {
                    false
                }
            } == true
        }
        Log.d("productDetails","purchaseSubscription $productDetails")
        productDetails?.let {
            Log.d("subscriptionOfferDetails","${it.subscriptionOfferDetails}")
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(it)
                    .setOfferToken(offerToken)
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

            // 添加日志檢查
            Log.d("BillingManager", "Billing Flow launched: ${billingResult.responseCode}")
            Log.d("BillingManager", "Billing Flow : $billingResult")
            // 在這裡處理訂閱結果，例如顯示進度指示器、處理錯誤等
            when (billingResult.responseCode) {
                BillingResponseCode.OK -> {
                    // 訂閱流程啟動成功
                    // ...
                }
                BillingResponseCode.USER_CANCELED -> {
                    // 使用者取消訂閱
                    // ...
                }
                else -> {
                    // 其他錯誤
                    // ...
                }
            }
        } ?: Log.e("BillingManager", "Product details not found for productId: $subscriptionPlanId")
    }
}