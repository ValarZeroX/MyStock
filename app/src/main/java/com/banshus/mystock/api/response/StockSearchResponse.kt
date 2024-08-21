package com.banshus.mystock.api.response

import com.google.gson.annotations.SerializedName

data class StockSearchResponse(
    @SerializedName("quotes")
    val quotes: List<QuoteItem>?,
    @SerializedName("news")
    val news: List<NewsItem>?
)

data class QuoteItem(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("shortname")
    val shortName: String,
    @SerializedName("longname")
    val longName: String?,
    @SerializedName("exchange")
    val exchange: String,
    @SerializedName("quoteType")
    val quoteType: String,
    @SerializedName("sector")
    val sector: String?,
    @SerializedName("industry")
    val industry: String?
)

data class NewsItem(
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("providerPublishTime")
    val providerPublishTime: Long
)
