package com.banshus.mystock
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateValueFormatter : ValueFormatter() {
    private val formatterDate = DateTimeFormatter.ofPattern("MM/dd")

    override fun getFormattedValue(value: Float): String {
        val dateTime = Instant.ofEpochMilli(value.toLong())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return dateTime.format(formatterDate)
    }
}