package com.banshus.mystock.api.response

import com.google.gson.annotations.SerializedName

data class StockChartResponse(
    @SerializedName("chart")
    val chart: Chart
)

data class Chart(
    @SerializedName("result")
    val result: List<Result>?,
    @SerializedName("error")
    val error: ErrorDetails?
)

data class Result(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("timestamp")
    val timestamp: List<Long>?, // Make nullable if not always present
    @SerializedName("indicators")
    val indicators: Indicators
)

data class Meta(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("regularMarketPrice")
    val regularMarketPrice: Double,
    @SerializedName("regularMarketDayHigh")
    val regularMarketDayHigh: Double,
    @SerializedName("regularMarketDayLow")
    val regularMarketDayLow: Double,
    @SerializedName("shortName")
    val shortName: String,
    @SerializedName("chartPreviousClose")
    val chartPreviousClose: Double,
    // Add other fields as needed
)

data class Indicators(
    @SerializedName("quote")
    val quote: List<Quote>
)

data class Quote(
    @SerializedName("close")
    val close: List<Double>
)

data class ErrorDetails(
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String
)
