package com.banshus.mystock.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarView(
    month: Date,
    date: List<Pair<Date, Boolean>>,  // 日期列表和标记状态
    displayNext: Boolean,
    displayPrev: Boolean,
    onClickNext: () -> Unit,
    onClickPrev: () -> Unit,
    onClick: (Date) -> Unit,
    startFromSunday: Boolean,
    modifier: Modifier = Modifier,
    selectedDate: Date,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (displayPrev)
                IconButton(onClick = onClickPrev, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                }
            if (displayNext)
                IconButton(onClick = onClickNext, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                }
            Text(
                text = month.formatToMonthString(),
//                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(Alignment.Center)
            )
        }
//        Spacer(modifier = Modifier.size(16.dp))
        CalendarGrid(
            date = date,
            onClick = onClick,
            startFromSunday = startFromSunday,
            modifier = Modifier
                .wrapContentHeight()
//                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            selectedDate
        )
    }
}

@Composable
private fun CalendarGrid(
    date: List<Pair<Date, Boolean>>,
    onClick: (Date) -> Unit,
    startFromSunday: Boolean,
    modifier: Modifier = Modifier,
    selectedDate: Date,
) {
    val weekdayFirstDay = date.first().first.formatToWeekDay()
    val weekdays = getWeekDays(startFromSunday)
    CalendarCustomLayout(modifier = modifier) {
        weekdays.forEach {
            WeekdayCell(weekday = it)
        }
        repeat(if (!startFromSunday) weekdayFirstDay - 2 else weekdayFirstDay - 1) {
            Spacer(modifier = Modifier)
        }
        date.forEach {
            CalendarCell(date = it.first, signal = it.second, onClick = { onClick(it.first) }, selectedDate = selectedDate)
        }
    }
}

@Composable
private fun CalendarCell(
    date: Date,
    signal: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedDate: Date,
) {
    val text = date.formatToCalendarDay()
    val isToday = isToday(date)  // 检查是否为今天
    val isSelected = selectedDate == date
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .border(
                width = 2.dp, // 边框宽度
                color = if (isToday || isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, // 今天的日期使用特定颜色，其他日期边框透明
                shape = CircleShape // 边框形状为圆形
            )
            .background(
                shape = RoundedCornerShape(8.dp),
                color = if (signal) Color(0xFFBB86FC) else MaterialTheme.colorScheme.surface,
            )
            .clip(if (isToday) CircleShape else RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        if (signal) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .background(
                        shape = CircleShape,
                        color = Color(0xFF6200EA).copy(alpha = 0.7f)
                    )
            )
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 16.sp
        )
    }
}

private fun isToday(date: Date): Boolean {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}

@Composable
private fun WeekdayCell(weekday: Int, modifier: Modifier = Modifier) {
    val text = weekday.getDayOfWeek3Letters()
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        Text(
            text = text.orEmpty(),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.BottomCenter),
            fontSize = 14.sp
        )
    }
}

private fun Date.formatToCalendarDay(): String = SimpleDateFormat("d", Locale.getDefault()).format(this)

private fun Date.formatToMonthString(): String = SimpleDateFormat("MMMM", Locale.getDefault()).format(this)

private fun Date.formatToWeekDay(): Int = Calendar.getInstance().apply { time = this@formatToWeekDay }.get(Calendar.DAY_OF_WEEK)

private fun Int.getDayOfWeek3Letters(): String? = Calendar.getInstance().apply {
    set(Calendar.DAY_OF_WEEK, this@getDayOfWeek3Letters)
}.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())

private fun getWeekDays(startFromSunday: Boolean): List<Int> {
    val lista = (1..7).toList()
    return if (startFromSunday) lista else lista.drop(1) + lista.take(1)
}

@Composable
private fun CalendarCustomLayout(
    modifier: Modifier = Modifier,
    horizontalGapDp: Dp = 2.dp,
    verticalGapDp: Dp = 2.dp,
    content: @Composable () -> Unit,
) {
    val horizontalGap = with(LocalDensity.current) { horizontalGapDp.roundToPx() }
    val verticalGap = with(LocalDensity.current) { verticalGapDp.roundToPx() }
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val totalWidthWithoutGap = constraints.maxWidth - (horizontalGap * 6)
        val singleWidth = totalWidthWithoutGap / 7

        val xPos: MutableList<Int> = mutableListOf()
        val yPos: MutableList<Int> = mutableListOf()
        var currentX = 0
        var currentY = 0
        measurables.forEach { _ ->
            xPos.add(currentX)
            yPos.add(currentY)
            if (currentX + singleWidth + horizontalGap > totalWidthWithoutGap) {
                currentX = 0
                currentY += singleWidth + verticalGap
            } else {
                currentX += singleWidth + horizontalGap
            }
        }

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(maxHeight = singleWidth, maxWidth = singleWidth))
        }

        layout(
            width = constraints.maxWidth,
            height = currentY + singleWidth + verticalGap,
        ) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(
                    x = xPos[index],
                    y = yPos[index],
                )
            }
        }
    }
}