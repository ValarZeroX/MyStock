package com.banshus.mystock

import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    fun formatNumber(number: Int): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }
    fun formatNumber(number: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }
}