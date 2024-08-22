package com.banshus.mystock

import com.github.mikephil.charting.formatter.ValueFormatter

class DateValueFormatter(
    private val dateLabels: Map<Float, String>
) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return dateLabels[value] ?: ""
    }
}

class PercentValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
//        return "${value.toInt()}%"  // 将值格式化为整数百分比
        return if (value % 2 == 0f) {
            value.toInt().toString() + "%"  // 只在偶数位置显示值
        } else {
            ""  // 其他位置不显示值
        }
    }
}
