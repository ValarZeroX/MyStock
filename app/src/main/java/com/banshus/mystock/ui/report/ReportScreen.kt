package com.banshus.mystock.ui.report

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.ui.tool.DateRangeType
import com.banshus.mystock.ui.tool.DateSwitcher
import com.banshus.mystock.ui.tool.RangeTypeSelectionDialog
import com.banshus.mystock.ui.tool.getStartAndEndDate
import com.banshus.mystock.viewmodels.StockAccountViewModel
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.banshus.mystock.viewmodels.StockSymbolViewModel
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun ReportScreen(
    navController: NavHostController,
    stockViewModel: StockViewModel,
    stockAccountViewModel: StockAccountViewModel,
    stockRecordViewModel: StockRecordViewModel,
    stockSymbolViewModel: StockSymbolViewModel
) {
    //DateSwitcher使用
    var startDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var endDate by remember { mutableStateOf(startDate.plusMonths(1).minusDays(1)) }
    val currentRangeType by remember { mutableStateOf(DateRangeType.MONTH) }

    var selectedReportTabIndex by stockViewModel.selectedReportTabIndex
//    val showDialog by stockViewModel.showRangeTypeDialog.observeAsState(false)

    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    stockRecordViewModel.loadRealizedGainsAndLossesForAllAccounts(startDateMillis, endDateMillis)
    val allAccountsRecord by stockRecordViewModel.realizedGainsAndLossesForAllAccounts.observeAsState(emptyMap())
    Log.d("allAccountsRecord", "$allAccountsRecord")

    Log.d("startDate", "$startDate")
    Log.d("endDate", "$endDate")
    Scaffold(
        topBar = {
            ReportHeader(stockViewModel)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Column {
                TabRow(
                    selectedTabIndex = selectedReportTabIndex,
//                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedReportTabIndex == 0,
                        onClick = { selectedReportTabIndex = 0 },
                        text = { Text("總覽") }
                    )
                    Tab(
                        selected = selectedReportTabIndex == 1,
                        onClick = { selectedReportTabIndex = 1 },
                        text = { Text("帳戶") }
                    )
                    Tab(
                        selected = selectedReportTabIndex == 2,
                        onClick = { selectedReportTabIndex = 2 },
                        text = { Text("市場") }
                    )
                    Tab(
                        selected = selectedReportTabIndex == 3,
                        onClick = { selectedReportTabIndex = 3 },
                        text = { Text("股票") }
                    )
                }
                DateSwitcher(
                    stockViewModel = stockViewModel,
                    initialDate = startDate,
                    initialRangeType = currentRangeType,
                    onDateChanged = { start, end ->
                        startDate = start
                        endDate = end
                    }
                )
                when (selectedReportTabIndex) {
                    0 -> {
                        Text("總覽")
                    }
                    1 -> {
                        Text("帳戶")
                    }
                    2 -> {
                        Text("市場")
                    }
                    3 -> {
                        Text("股票")
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHeader(
    stockViewModel: StockViewModel,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "報表",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                stockViewModel.showDialog()
            }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "range"
                )
            }
        },
//        actions = {
//            IconButton(onClick = {
//            }) {
//                Icon(
//                    imageVector = Icons.Filled.Save,
//                    contentDescription = "儲存"
//                )
//            }
//        }
    )
}