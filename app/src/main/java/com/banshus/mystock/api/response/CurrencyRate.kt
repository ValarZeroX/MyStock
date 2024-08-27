package com.banshus.mystock.api.response

import com.google.gson.annotations.SerializedName

data class CurrencyRate(
    @SerializedName("Exrate") val exchangeRate: Double,
    @SerializedName("UTC") val utc: String
)

typealias CurrencyRatesResponse = Map<String, CurrencyRate>
