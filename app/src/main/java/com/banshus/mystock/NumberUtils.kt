package com.banshus.mystock

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    fun formatNumber(number: Int): String {
        val decimalFormat = DecimalFormat("#,##0.00")
        return decimalFormat.format(number)
//        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
//        return numberFormat.format(number)
    }
    fun formatNumber(number: Double): String {
        val decimalFormat = DecimalFormat("#,##0.00")
        return decimalFormat.format(number)
//        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
//        return numberFormat.format(number)
    }
}