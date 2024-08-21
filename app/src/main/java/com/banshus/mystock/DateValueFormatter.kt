package com.banshus.mystock
import android.util.Log
import com.banshus.mystock.ui.tool.DateRangeType
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateValueFormatter(
    private val currentRangeType: DateRangeType
) : ValueFormatter() {
    private val formatterYear = DateTimeFormatter.ofPattern("yyyy")
    private val formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM")
    private val formatterMonthDay = DateTimeFormatter.ofPattern("MM/dd")

    override fun getFormattedValue(value: Float): String {
        val dateTime = Instant.ofEpochMilli(value.toLong())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        Log.d("dateTime", "$dateTime") // Debugging output to check formatted date

        return when (currentRangeType) {
            DateRangeType.ALL -> {
                // 按年分组时，显示年份
                dateTime.format(formatterYear)
            }
            DateRangeType.YEAR -> {
                // 按月分组时，显示年月
                dateTime.format(formatterMonth)
            }
            DateRangeType.MONTH -> {
                // 按日分组时，显示月日
                dateTime.format(formatterMonthDay)
            }
            else -> {
                // 默认显示月日
                dateTime.format(formatterMonthDay)
            }
        }
    }
}

class PercentValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}%"  // 将值格式化为整数百分比
    }
}