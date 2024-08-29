package com.banshus.mystock.ui.report

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.getProfitColor
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.Currency
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.ui.theme.StockGreen
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.ui.tool.DateRangeType
import com.banshus.mystock.viewmodels.DetailedStockMetrics
import com.banshus.mystock.viewmodels.StockRecordViewModel
import com.google.type.Color
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AllReportScreen(
    stockRecordViewModel: StockRecordViewModel,
    stockViewModel: StockViewModel,
    includeCommission: Boolean,
    includeTransactionTax: Boolean,
    includeDividends : Boolean,
    allCurrencies: List<Currency>?,
    stockAccounts: Map<Int, StockAccount>,
    currentRangeType: DateRangeType
) {
    val startDate by stockViewModel.startDate.observeAsState(LocalDate.now().withDayOfMonth(1))
    val endDate by stockViewModel.endDate.observeAsState(startDate.plusMonths(1).minusDays(1))
    val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endDateMillis = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val totalDividends by stockRecordViewModel.getAllTotalDividendsByDateRangeAndAccount(
        startDate = startDateMillis,
        endDate = endDateMillis,
        allCurrencies= allCurrencies,
        stockAccounts = stockAccounts
    ).observeAsState(0.0)

    val totalMetrics by stockRecordViewModel.calculateTotalMetricsForAllAccounts(
        startDateMillis,
        endDateMillis,
        includeCommission = includeCommission, // Adjust as per user settings
        includeTransactionTax = includeTransactionTax, // Adjust as per user settings
        includeDividends = includeDividends, // Adjust as per user settings
        totalDividends = totalDividends,
        allCurrencies= allCurrencies,
        stockAccounts = stockAccounts
    ).observeAsState(DetailedStockMetrics(0.0, 0.0, 0.0, 0.0, 0.0, 0.0))

    val annualizedReturn = stockRecordViewModel.calculateAnnualizedReturnWithoutDividends(
        accountMetrics = totalMetrics,
        startDateMillis = startDateMillis,
        endDateMillis = endDateMillis,
        includeCommission = includeCommission,
        includeTransactionTax = includeTransactionTax,
        includeDividends = includeDividends,
        totalDividends = totalDividends
    )

//    Log.d("allCurrencies", "$allCurrencies")
    val profitColor = getProfitColor(
        totalMetrics.totalProfit,
        StockRed,
        StockGreen,
        MaterialTheme.colorScheme.onSurface
    )
    val annualizedColor = getProfitColor(
        annualizedReturn,
        StockRed,
        StockGreen,
        MaterialTheme.colorScheme.onSurface
    )
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        Row {
            Text(text = "總買進", modifier = Modifier.weight(1f))
            Text(text = "總賣出", modifier = Modifier.weight(1f))
        }
        Row {
            Text(
                text = formatNumber(totalMetrics.totalCostBasis),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatNumber(totalMetrics.totalSellIncome),
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            Text(text = "總手續費", modifier = Modifier.weight(1f))
            Text(text = "總交易稅", modifier = Modifier.weight(1f))
        }
        Row {
            Text(
                text = formatNumber(totalMetrics.totalCommission),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatNumber(totalMetrics.totalTransactionTax),
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            Text(text = "總股利", modifier = Modifier.weight(1f))
            Text(text = "年化報酬率", modifier = Modifier.weight(1f))
        }
        Row {
            Text(
                text = formatNumber(totalDividends),
                modifier = Modifier.weight(1f)
            )
            if (currentRangeType == DateRangeType.YEAR || currentRangeType == DateRangeType.ALL) {
                Text(
                    text = "${formatNumber(annualizedReturn)}%",
                    modifier = Modifier.weight(1f),
                    color = annualizedColor
                )
            } else {
                Text(
                    text = "-",
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row {
            Text(text = "總損益", modifier = Modifier.weight(1f))
            Text(text = "總損益率", modifier = Modifier.weight(1f))
        }
        Row {
            Text(
                text = formatNumber(totalMetrics.totalProfit),
                modifier = Modifier.weight(1f),
                color = profitColor
            )
            Text(
                text = "${formatNumber(totalMetrics.totalProfitPercent)}%",
                modifier = Modifier.weight(1f),
                color = profitColor
            )

        }
        // Display total metrics
//        Row(label = "總買進", value = formatNumber(totalMetrics.totalCostBasis))
//        Row(label = "總賣出", value = formatNumber(totalMetrics.totalSellIncome))
//        Row(label = "總手續費", value = formatNumber(totalMetrics.totalCommission))
//        Row(label = "總交易稅", value = formatNumber(totalMetrics.totalTransactionTax))
//        Row(label = "總股利", value = formatNumber(totalDividends))
//        Row(label = "總損益", value = formatNumber(totalMetrics.totalProfit))
//        Row(label = "總損益率", value = "${formatNumber(totalMetrics.totalProfitPercent)}%")
//        MetricsRow(label = "年化報酬率", value = "${formatNumber(annualizedReturn)}%")

        // Display individual account metrics
        LazyColumn {
//            items(stockRecordViewModel.calculateTotalCostAndProfitForAllAccounts().value ?: emptyMap()) { (accountId, metrics) ->
//                Text(text = "Account ID: $accountId")
////                MetricsRow(label = "總買進", value = formatNumber(metrics.totalCostBasis))
////                MetricsRow(label = "總賣出", value = formatNumber(metrics.totalPrice))
////                MetricsRow(label = "總手續費", value = formatNumber(metrics.totalCommission))
////                MetricsRow(label = "總交易稅", value = formatNumber(metrics.totalTransactionTax))
////                MetricsRow(label = "總股利", value = formatNumber(totalDividends))
////                MetricsRow(label = "總損益", value = formatNumber(metrics.totalProfit))
////                MetricsRow(label = "總損益率", value = "${formatNumber(metrics.totalProfitPercent)}%")
////                MetricsRow(label = "年化報酬率", value = "${formatNumber(annualizedReturn)}%")
////                Spacer(modifier = Modifier.height(8.dp))
//            }
        }
    }
}

//@Composable
//fun MetricsRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
//    Row(modifier = Modifier.fillMaxWidth()) {
//        Text(text = label, modifier = Modifier.weight(1f))
//        Text(text = value, modifier = Modifier.weight(1f), color = color)
//    }
//}