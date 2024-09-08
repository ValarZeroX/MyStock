package com.banshus.mystock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.banshus.mystock.billing.BillingManager

class BillingViewModel(private val billingManager: BillingManager) : ViewModel() {
    val skuDetailsList = billingManager.subscriptions.asLiveData()

    fun startBillingConnection() {
        billingManager.billingSetup() // 建立 Billing 連接
    }

    fun hasSubscription() {
        billingManager.hasSubscription()
    }

    fun checkSubscriptionStatus(subscriptionPlanId: String) {
        billingManager.checkSubscriptionStatus(subscriptionPlanId)
    }
}

class BillingViewModelFactory(private val billingManager: BillingManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillingViewModel(billingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}