package com.banshus.mystock.ui.tool

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banshus.mystock.StockViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun MonthSwitcher(onMonthChanged: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            currentMonth = currentMonth.minusMonths(1)
            onMonthChanged(currentMonth)
        }) {
            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month")
        }

        Text(
            text = "${formatter.format(currentMonth)} ~ ${
                formatter.format(
                    currentMonth.plusMonths(1).minusDays(1)
                )
            }",
        )

        IconButton(onClick = {
            currentMonth = currentMonth.plusMonths(1)
            onMonthChanged(currentMonth)
        }) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month")
        }
    }
}

enum class DateRangeType(val displayName: String) {
    YEAR("年"),
    MONTH("月"),
    WEEK("週")
}

@Composable
fun DateSwitcher(
    stockViewModel: StockViewModel,
    initialDate: LocalDate,
    initialRangeType: DateRangeType,
    onDateChanged: (LocalDate, LocalDate) -> Unit
) {
    var currentDate by remember { mutableStateOf(initialDate) }
    var currentRangeType by remember { mutableStateOf(initialRangeType) }
    val showDialog by stockViewModel.showRangeTypeDialog.observeAsState(false)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            currentDate = when (currentRangeType) {
                DateRangeType.YEAR -> currentDate.minusYears(1)
                DateRangeType.MONTH -> currentDate.minusMonths(1)
                DateRangeType.WEEK -> currentDate.minusWeeks(1)
            }
            val (startDate, endDate) = getStartAndEndDate(currentRangeType, currentDate)
            onDateChanged(startDate, endDate)
        }) {
            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous")
        }

        val (startDate, endDate) = getStartAndEndDate(currentRangeType, currentDate)

        Text(
            text = "${formatter.format(startDate)} ~ ${formatter.format(endDate)}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {

            }
        )

        IconButton(onClick = {
            currentDate = when (currentRangeType) {
                DateRangeType.YEAR -> currentDate.plusYears(1)
                DateRangeType.MONTH -> currentDate.plusMonths(1)
                DateRangeType.WEEK -> currentDate.plusWeeks(1)
            }
            val (newStartDate, newEndDate) = getStartAndEndDate(currentRangeType, currentDate)
            onDateChanged(newStartDate, newEndDate)
        }) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next")
        }
        if (showDialog) {
            RangeTypeSelectionDialog(
                currentRangeType = currentRangeType,
                onRangeTypeSelected = { selectedType ->
                    currentRangeType = selectedType
                    stockViewModel.hideDialog()
                    val (newStartDate, newEndDate) = getStartAndEndDate(selectedType, currentDate)
                    onDateChanged(newStartDate, newEndDate)
                },
                onDismiss = { stockViewModel.hideDialog() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )


    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                onDateSelected(selectedDateMillis)
                onDismiss()
            }) {
                Text("確定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
fun RangeTypeSelectionDialog(
    currentRangeType: DateRangeType,
    onRangeTypeSelected: (DateRangeType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "選擇日期範圍")
        },
        text = {
            Column {
                // 用 RadioButton 組合來實現選擇功能
                DateRangeType.entries.forEach { rangeType ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRangeTypeSelected(rangeType) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = rangeType == currentRangeType,
                            onClick = { onRangeTypeSelected(rangeType) }
                        )
                        Text(text = rangeType.displayName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("確定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

//fun getEndDate(startDate: LocalDate, rangeType: DateRangeType): LocalDate {
//    return when (rangeType) {
//        DateRangeType.YEAR -> startDate.plusYears(1).minusDays(1)
//        DateRangeType.MONTH -> startDate.plusMonths(1).minusDays(1)
//        DateRangeType.WEEK -> startDate.plusWeeks(1).minusDays(1)
//    }
//}

// 計算開始和結束日期的函數
fun getStartAndEndDate(rangeType: DateRangeType, baseDate: LocalDate): Pair<LocalDate, LocalDate> {
    return when (rangeType) {
        DateRangeType.YEAR -> {
            val startDate = baseDate.withDayOfYear(1)
            val endDate = startDate.plusYears(1).minusDays(1)
            startDate to endDate
        }

        DateRangeType.MONTH -> {
            val startDate = baseDate.withDayOfMonth(1)
            val endDate = startDate.plusMonths(1).minusDays(1)
            startDate to endDate
        }

        DateRangeType.WEEK -> {
            val startDate = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endDate = startDate.plusDays(6)
            startDate to endDate
        }
    }
}