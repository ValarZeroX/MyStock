package com.banshus.mystock.api.service

import com.banshus.mystock.api.response.CurrencyRatesResponse
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("capi.php")
    suspend fun getCurrencyRates(): CurrencyRatesResponse
}
