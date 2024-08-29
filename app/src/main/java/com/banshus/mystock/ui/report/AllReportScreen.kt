package com.banshus.mystock.ui.report

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banshus.mystock.NumberUtils.formatNumber
import com.banshus.mystock.NumberUtils.getProfitColor
import com.banshus.mystock.SharedOptions.optionStockMarket
import com.banshus.mystock.StockViewModel
import com.banshus.mystock.data.entities.Currency
import com.banshus.mystock.data.entities.StockAccount
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.theme.StockGreen
import com.banshus.mystock.ui.theme.StockRed
import com.banshus.mystock.ui.tool.DateRangeType
import com.banshus.mystock.viewmodels.DetailedStockMetrics
import com.banshus.mystock.viewmodels.StockRecordViewModel
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
    currentRangeType: DateRangeType,
    allAccountsRecord: Map<Int, DetailedStockMetrics>,
    annualizedGroupByAccount: Map<Int, Double>,
    mainCurrency: String,
    onTabSelected: (Int, Int, StockAccount?) -> Unit
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

//    Log.d("allAccountsRecord", "$allAccountsRecord")
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
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "全部帳戶", fontSize = 24.sp, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Gray1,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(
                        top = 1.dp,
                        bottom = 1.dp,
                        start = 10.dp,
                        end = 10.dp
                    )
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.AttachMoney,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = mainCurrency,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
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
        LazyColumn {
            items(allAccountsRecord.toList()) { (accountId, metrics) ->
                ListItem(
                    modifier = Modifier.clickable {
                        onTabSelected(1, accountId, stockAccounts[accountId])
                    },
                    headlineContent = { Text(stockAccounts[accountId]!!.account) },
                    supportingContent = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "總損益", modifier = Modifier.weight(1f))
                                Text(text = "總損益率", modifier = Modifier.weight(1f))
                                Text(text = "年化報酬率", modifier = Modifier.weight(1f))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = formatNumber(metrics.totalProfit), modifier = Modifier.weight(1f))
                                Text(text = "${formatNumber(metrics.totalProfitPercent)}%", modifier = Modifier.weight(1f))
                                if (currentRangeType == DateRangeType.YEAR || currentRangeType == DateRangeType.ALL) {
                                    Text(
                                        text = "${formatNumber(annualizedGroupByAccount[accountId]!!)}%",
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
                        }
                    },
                    trailingContent = {
                        Column {
                            Text(
                                text = stockAccounts[accountId]!!.currency,
                                modifier = Modifier
                                    .padding(4.dp)  // 添加一些内边距
                                    .border(
                                        width = 2.dp,  // 边框宽度
                                        color = MaterialTheme.colorScheme.primary,  // 边框颜色
                                        shape = RoundedCornerShape(50)  // 椭圆形边框
                                    )
                                    .clip(RoundedCornerShape(50))  // 将内容裁剪为椭圆形
                                    .padding(horizontal = 12.dp, vertical = 6.dp)  // 内部文本的填充
                            )
                            Text(
                                text = optionStockMarket[stockAccounts[accountId]!!.stockMarket],
                                modifier = Modifier
                                    .padding(4.dp)  // 添加一些内边距
                                    .border(
                                        width = 2.dp,  // 边框宽度
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clip(RoundedCornerShape(50))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
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