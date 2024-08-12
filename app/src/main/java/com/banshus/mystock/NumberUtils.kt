package com.banshus.mystock

import androidx.compose.ui.graphics.Color
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    fun formatNumber(number: Int): String {
        val decimalFormat = if (number % 100 == 0) {
            DecimalFormat("#,##0")
        } else {
            DecimalFormat("#,##0.00")
        }
        return decimalFormat.format(number)
    }
    fun formatNumber(number: Double): String {
        val decimalFormat = if (number % 1 == 0.0) {
            DecimalFormat("#,##0")
        } else {
            DecimalFormat("#,##0.00")
        }
        return decimalFormat.format(number)
    }

    fun getProfitColor(value: Double, positiveColor: Color, negativeColor: Color, defaultColor: Color): Color {
        return when {
            value > 0 -> positiveColor
            value < 0 -> negativeColor
            else -> defaultColor
        }
    }
}