package com.banshus.mystock

import android.content.Context

class CurrencyUtils {
    companion object {
        fun getCurrencyName(context: Context, code: String): String {
            return when (code) {
                "AUD" -> context.getString(R.string.currency_aud)
                "BRL" -> context.getString(R.string.currency_brl)
                "CAD" -> context.getString(R.string.currency_cad)
                "CHF" -> context.getString(R.string.currency_chf)
                "CZK" -> context.getString(R.string.currency_czk)
                "DKK" -> context.getString(R.string.currency_dkk)
                "EUR" -> context.getString(R.string.currency_eur)
                "GBP" -> context.getString(R.string.currency_gbp)
                "HKD" -> context.getString(R.string.currency_hkd)
                "HUF" -> context.getString(R.string.currency_huf)
                "ILS" -> context.getString(R.string.currency_ils)
                "JPY" -> context.getString(R.string.currency_jpy)
                "MXN" -> context.getString(R.string.currency_mxn)
                "MYR" -> context.getString(R.string.currency_myr)
                "NOK" -> context.getString(R.string.currency_nok)
                "NZD" -> context.getString(R.string.currency_nzd)
                "PHP" -> context.getString(R.string.currency_php)
                "PLN" -> context.getString(R.string.currency_pln)
                "RMB" -> context.getString(R.string.currency_rmb)
                "RUB" -> context.getString(R.string.currency_rub)
                "SEK" -> context.getString(R.string.currency_sek)
                "SGD" -> context.getString(R.string.currency_sgd)
                "THB" -> context.getString(R.string.currency_thb)
                "TRY" -> context.getString(R.string.currency_try)
                "TWD" -> context.getString(R.string.currency_twd)
                "USD" -> context.getString(R.string.currency_usd)
                // 添加其他貨幣代碼
                else -> code // 如果沒有對應的名稱，返回代碼本身
            }
        }
    }
}