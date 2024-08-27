package com.banshus.mystock.repository

import android.util.Log
import com.banshus.mystock.api.response.CurrencyRatesResponse
import com.banshus.mystock.api.service.CurrencyApiService
import retrofit2.HttpException

class CurrencyApiRepository (private val apiService: CurrencyApiService) {

    suspend fun fetchCurrencyRates(): CurrencyRatesResponse? {
        return try {
            apiService.getCurrencyRates()
        } catch (e: HttpException) {
            Log.e("CurrencyRepository", "HTTP error: ${e.message()}", e)
            null
        } catch (e: Exception) {
            Log.e("CurrencyRepository", "Error: ${e.message}", e)
            null
        }
    }
}